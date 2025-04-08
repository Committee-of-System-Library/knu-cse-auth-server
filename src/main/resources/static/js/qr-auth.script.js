let scanning = false;
let scannedStudents = new Map();
let lastScanTime = 0;
const SCAN_COOLDOWN_MS = 3000;

document.addEventListener("DOMContentLoaded", () => {
  const startBtn = document.getElementById("startBtn");
  const endBtn = document.getElementById("endBtn");
  const saveLogBtn = document.getElementById("saveLogBtn");

  startBtn.addEventListener("click", () => {
    startBtn.disabled = true;
    endBtn.disabled = false;
    saveLogBtn.disabled = false;

    scanning = true;
    startCamera()
      .then((videoElement) => scanLoop(videoElement))
      .catch((err) => {
        scanning = false;
        startBtn.disabled = false;
        alert("카메라를 시작할 수 없습니다.\n" + err);
      });
  });

  endBtn.addEventListener("click", () => {
    showEndConfirmPopup();
  });

  saveLogBtn.addEventListener("click", () => {
    if (scannedStudents.size === 0) {
      showPopup("인증된 학생이 없습니다.", "알림");
      return;
    }
    const dataArray = Array.from(scannedStudents.values());
    saveLogsToServer(dataArray);
  });
});

async function startCamera() {
  const cameraPreview = document.getElementById("cameraPreview");
  const constraints = {
    video: { facingMode: "environment" },
    audio: false,
  };
  const stream = await navigator.mediaDevices.getUserMedia(constraints);
  cameraPreview.srcObject = stream;
  await cameraPreview.play();
  return cameraPreview;
}

function stopCamera() {
  const cameraPreview = document.getElementById("cameraPreview");
  const stream = cameraPreview.srcObject;
  if (stream) {
    stream.getTracks().forEach((track) => track.stop());
  }
  cameraPreview.srcObject = null;
}

function scanLoop(videoElement) {
  const canvas = document.createElement("canvas");
  const ctx = canvas.getContext("2d");

  function tick() {
    if (!scanning) return;

    if (videoElement.readyState === videoElement.HAVE_ENOUGH_DATA) {
      canvas.width = videoElement.videoWidth;
      canvas.height = videoElement.videoHeight;
      ctx.drawImage(videoElement, 0, 0, canvas.width, canvas.height);

      const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
      const qrCode = jsQR(imageData.data, canvas.width, canvas.height, {
        inversionAttempts: "dontInvert",
      });

      if (qrCode && qrCode.data) {
        handleDecodedData(qrCode.data);
      }
    }
    requestAnimationFrame(tick);
  }

  requestAnimationFrame(tick);
}

function handleDecodedData(qrString) {
  const now = performance.now();
  if (now - lastScanTime < SCAN_COOLDOWN_MS) return;
  lastScanTime = now;

  if (qrString.length < 10) {
    showPopup("QR 인식 오류(길이가 너무 짧음)", "오류");
    return;
  }

  const studentNumber = qrString.substring(0, 10).trim();
  if (scannedStudents.has(studentNumber)) {
    playTts("이미 스캔된 학생입니다.");
    showPopup(`이미 스캔된 학생 (${studentNumber})`, "중복");
    return;
  }

  const duesOnly = document.getElementById("duesOnlyCheck").checked;
  const url = `/auth/qr-auth/api/student?studentNumber=${encodeURIComponent(
    studentNumber
  )}&duesOnly=${duesOnly}`;

  fetch(url)
    .then((res) => {
      if (!res.ok) throw res;
      return res.json();
    })
    .then((json) => {
      const { studentNumber, studentName, duesPaid } = json.data;
      scannedStudents.set(studentNumber, {
        studentNumber,
        studentName,
        duesPaid,
      });
      addScannedListItem(studentNumber, studentName, duesPaid);
      playTts(`${studentName}님, 인증이 완료되었습니다.`);
    })
    .catch((err) => {
      playTts("인증을 실패했습니다.");
      showPopup("QR 인증 중 오류가 발생했습니다.", "오류");
      console.error("QR 인증 오류:", err);
    });
}

function addScannedListItem(studentNumber, studentName, duesPaid) {
  const scannedListTbody = document.getElementById("scannedList");
  const tr = document.createElement("tr");

  const tdStudentNumber = document.createElement("td");
  tdStudentNumber.textContent = studentNumber;
  tr.appendChild(tdStudentNumber);

  const tdStudentName = document.createElement("td");
  tdStudentName.textContent = studentName;
  tr.appendChild(tdStudentName);

  const tdDues = document.createElement("td");
  tdDues.textContent = duesPaid ? "O" : "X";
  tr.appendChild(tdDues);

  scannedListTbody.appendChild(tr);
}

function playTts(text) {
  const msg = new SpeechSynthesisUtterance(text);
  const voices = window.speechSynthesis.getVoices();
  const selectedVoice = voices.find(
    (voice) => voice.lang === "ko-KR" && voice.name.includes("Google")
  );
  if (selectedVoice) {
    msg.voice = selectedVoice;
  }
  msg.rate = 1;
  msg.pitch = 1;
  window.speechSynthesis.speak(msg);
}

function saveLogsToServer(scannedArray) {
  const body = { scannedStudents: scannedArray };

  fetch(`/auth/qr-auth/api/logs`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  })
    .then((res) => {
      if (!res.ok) throw res;
      return res.json();
    })
    .then(() => {
      showPopup("QR 스캔 로그가 저장되었습니다.", "알림");
    })
    .catch((err) => {
      showPopup("로그 저장 중 오류가 발생했습니다.", "오류");
      console.error(err);
    });
}

function endScanning() {
  scanning = false;
  document.getElementById("endBtn").disabled = true;
  document.getElementById("saveLogBtn").disabled = true;
  stopCamera();
  scannedStudents.clear();
  document.getElementById("scannedList").innerHTML = "";
  showPopup("카메라를 종료했습니다.", "알림");
}

function showEndConfirmPopup() {
  const modal = document.getElementById("myModal");
  const modalMessage = document.getElementById("modalMessage");
  const modalBody = document.getElementById("modalBody");
  const modalButtons = document.getElementById("modalButtons");

  modalMessage.textContent = "경고";
  modalBody.textContent = "정말 종료하시겠습니까?";
  modalButtons.innerHTML = `
        <button id="saveAndEndBtn">저장 후 종료</button>
        <button id="endWithoutSaveBtn">그냥 종료</button>
        <button id="cancelEndBtn">취소</button>
    `;
  modal.style.display = "block";

  document.getElementById("saveAndEndBtn").onclick = () => {
    closeModal();
    if (scannedStudents.size > 0) {
      const dataArray = Array.from(scannedStudents.values());
      saveLogsToServer(dataArray);
    }
    endScanning();
  };

  document.getElementById("endWithoutSaveBtn").onclick = () => {
    closeModal();
    endScanning();
  };

  document.getElementById("cancelEndBtn").onclick = () => {
    closeModal();
  };
}

function showPopup(message, title = "알림") {
  const modal = document.getElementById("myModal");
  const modalMessage = document.getElementById("modalMessage");
  const modalBody = document.getElementById("modalBody");
  const modalButtons = document.getElementById("modalButtons");

  modalMessage.textContent = title;
  modalBody.textContent = message;
  modalButtons.innerHTML = `<button onclick="closeModal()">닫기</button>`;
  modal.style.display = "block";
}

function closeModal() {
  const modal = document.getElementById("myModal");
  modal.style.display = "none";
}
