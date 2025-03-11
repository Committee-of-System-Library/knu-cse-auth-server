function checkStudentNumber() {
    const studentNumberInput = document.getElementById('studentNumber');
    const studentNumber = studentNumberInput.value.trim();

    if (!studentNumber) {
        showPopup("학번을 입력해주세요.");
        return;
    }

    fetch(`/additional-info/check?studentNumber=${encodeURIComponent(studentNumber)}`)
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw err; });
            }
            return response.json();
        })
        .then(data => {
            const { name, studentNumber } = data;
            showConfirmPopup(
                `${name}님, 학번 ${studentNumber}이 맞습니까?`,
                () => connectStudent(studentNumber)
            );
        })
        .catch(err => {
            showPopup(err.message || "학번 확인 중 오류가 발생했습니다.");
        });
}

function connectStudent(studentNumber) {
    const formData = new FormData();
    formData.append("studentNumber", studentNumber);

    fetch("/additional-info/connect", {
        method: "POST",
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw err; });
            }
            return response.json();
        })
        .then(data => {
            // data = { message, redirectUrl }
            if (data.redirectUrl) {
                window.location.href = data.redirectUrl;
            } else {
                // redirectUrl 없으면 메시지 모달로 표시
                showPopup(data.message || "연결 성공 (리다이렉트 URL 없음)");
            }
        })
        .catch(err => {
            showPopup(err.message || "연결 중 오류가 발생했습니다.");
        });
}

function showPopup(message) {
    const modal = document.getElementById("myModal");
    const modalMessage = document.getElementById("modalMessage");
    const modalBody = document.getElementById("modalBody");
    const modalButtons = document.getElementById("modalButtons");

    modalMessage.textContent = "알림";
    modalBody.textContent = message;
    modalButtons.innerHTML = `<button onclick="closeModal()">닫기</button>`;

    modal.style.display = "block";
}

function showConfirmPopup(message, onConfirm) {
    const modal = document.getElementById("myModal");
    const modalMessage = document.getElementById("modalMessage");
    const modalBody = document.getElementById("modalBody");
    const modalButtons = document.getElementById("modalButtons");

    modalMessage.textContent = "확인 요청";
    modalBody.textContent = message;
    modalButtons.innerHTML = `
    <button id="confirmBtn">확인</button>
    <button id="cancelBtn">취소</button>
  `;

    modal.style.display = "block";

    document.getElementById("confirmBtn").onclick = () => {
        closeModal();
        if (typeof onConfirm === "function") {
            onConfirm();
        }
    };
    document.getElementById("cancelBtn").onclick = () => {
        closeModal();
    };
}

function closeModal() {
    const modal = document.getElementById("myModal");
    modal.style.display = "none";
}
