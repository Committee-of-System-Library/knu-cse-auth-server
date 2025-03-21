function checkStudentNumber() {
    const studentNumberInput = document.getElementById("studentNumber");
    const studentNumber = studentNumberInput.value.trim();

    if (!studentNumber) {
        showPopup("학번을 입력해주세요.");
        return;
    }

    fetch(
        `/auth/additional-info/check?studentNumber=${encodeURIComponent(
            studentNumber
        )}`
    )
        .then((response) => {
            if (!response.ok) {
                return response.json().then((err) => {
                    throw err;
                });
            }
            return response.json();
        })
        .then((data) => {
            const {name, studentNumber} = data;
            showConfirmPopup(`${name}님, 학번 ${studentNumber}이 맞습니까?`, () =>
                connectStudent(studentNumber)
            );
        })
        .catch((err) => {
            showPopup(err.message || "학번 확인 중 오류가 발생했습니다.");
        });
}

function connectStudent(studentNumber) {
    const formData = new FormData();
    formData.append("studentNumber", studentNumber);

    fetch("/auth/additional-info/connect", {
        method: "POST",
        body: formData,
    })
        .then((res) => {
            if (!res.ok) throw new Error("연결 오류");
            return res.json();
        })
        .then((data) => {
            window.location.href = data.redirectUrl;
        })
        .catch((err) => {
            console.error(err);
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
