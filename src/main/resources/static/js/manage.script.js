// 간단 예시. 실제로는 CSRF나 인증 헤더(JWT 등) 처리가 필요할 수 있음.
async function updateStudent(button) {
    const row = button.closest("tr");
    const studentId = row.getAttribute("data-student-id");

    const studentNumber = row.querySelector("[data-field='studentNumber']").innerText.trim();
    const name = row.querySelector("[data-field='name']").innerText.trim();
    const majorSelect = row.querySelector("select[data-field='major']");
    const major = majorSelect.value;
    const roleSelect = row.querySelector("select[data-field='role']");
    const role = roleSelect.value;

    // 간단 PATCH 요청 예시
    try {
        const response = await fetch(`/api/students/${studentId}`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ studentNumber, name, major, role })
        });
        if (!response.ok) {
            const errorData = await response.json();
            alert(`[학생 수정 실패]\n${errorData.msg || "에러"}`);
            return;
        }
        alert("학생 정보가 수정되었습니다.");
    } catch (e) {
        console.error(e);
        alert("수정 도중 오류가 발생했습니다.");
    }
}

async function deleteStudent(button) {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    const row = button.closest("tr");
    const studentId = row.getAttribute("data-student-id");

    try {
        const response = await fetch(`/api/students/${studentId}`, {
            method: "DELETE"
        });
        if (!response.ok) {
            const errorData = await response.json();
            alert(`[학생 삭제 실패]\n${errorData.msg || "에러"}`);
            return;
        }
        alert("삭제되었습니다.");
        row.remove();
    } catch (e) {
        console.error(e);
        alert("삭제 도중 오류가 발생했습니다.");
    }
}

function openStudentModal() {
    document.getElementById("studentModal").style.display = "block";
}

async function createStudent() {
    const studentNumber = document.getElementById("newStudentNumber").value.trim();
    const name = document.getElementById("newStudentName").value.trim();
    const major = document.getElementById("newStudentMajor").value;
    const role = document.getElementById("newStudentRole").value;

    if (!studentNumber || !name) {
        alert("학번, 이름을 모두 입력해주세요.");
        return;
    }

    try {
        const response = await fetch(`/api/students`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ studentNumber, name, major, role })
        });
        if (!response.ok) {
            const errorData = await response.json();
            alert(`[학생 추가 실패]\n${errorData.msg || "에러"}`);
            return;
        }
        alert("학생이 추가되었습니다. 페이지를 새로고침합니다.");
        location.reload();
    } catch (e) {
        console.error(e);
        alert("추가 도중 오류가 발생했습니다.");
    }
}

// ---------------------------
// Dues(회비) 관련
// ---------------------------
async function updateDues(button) {
    const row = button.closest("tr");
    const duesId = row.getAttribute("data-dues-id");

    const depositorName = row.querySelector("[data-field='depositorName']").innerText.trim();
    const amount = row.querySelector("[data-field='amount']").innerText.trim();
    const remainingSemesters = row.querySelector("[data-field='remainingSemesters']").innerText.trim();

    try {
        const response = await fetch(`/api/dues/${duesId}`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ depositorName, amount: Number(amount), remainingSemesters: Number(remainingSemesters) })
        });
        if (!response.ok) {
            const errorData = await response.json();
            alert(`[회비 납부 내역 수정 실패]\n${errorData.msg || "에러"}`);
            return;
        }
        alert("회비 납부 정보가 수정되었습니다.");
    } catch (e) {
        console.error(e);
        alert("수정 도중 오류가 발생했습니다.");
    }
}

async function deleteDues(button) {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    const row = button.closest("tr");
    const duesId = row.getAttribute("data-dues-id");

    try {
        const response = await fetch(`/api/dues/${duesId}`, { method: "DELETE" });
        if (!response.ok) {
            const errorData = await response.json();
            alert(`[회비 납부 내역 삭제 실패]\n${errorData.msg || "에러"}`);
            return;
        }
        alert("삭제되었습니다.");
        row.remove();
    } catch (e) {
        console.error(e);
        alert("삭제 도중 오류가 발생했습니다.");
    }
}

function openDuesModal() {
    document.getElementById("duesModal").style.display = "block";
}

async function createDues() {
    const studentId = document.getElementById("duesStudentId").value.trim();
    const depositorName = document.getElementById("depositorName").value.trim();
    const amount = Number(document.getElementById("amount").value.trim());
    const remainingSemesters = Number(document.getElementById("remainingSemesters").value.trim());

    if (!studentId) {
        alert("연결할 학생의 ID를 입력해주세요.");
        return;
    }
    if (!depositorName || !amount || !remainingSemesters) {
        alert("필수 정보(입금자명, 금액, 남은 학기 수)를 모두 입력해주세요.");
        return;
    }

    try {
        const response = await fetch(`/api/dues`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                studentId: Number(studentId),
                depositorName,
                amount,
                remainingSemesters
            })
        });
        if (!response.ok) {
            const errorData = await response.json();
            alert(`[회비 납부 추가 실패]\n${errorData.msg || "에러"}`);
            return;
        }
        alert("회비 납부 정보가 추가되었습니다. 페이지를 새로고침합니다.");
        location.reload();
    } catch (e) {
        console.error(e);
        alert("추가 도중 오류가 발생했습니다.");
    }
}
