const contextPath = "/auth";

function showSection(sectionId, menuItem) {
    document.getElementById("studentSection").style.display = "none";
    document.getElementById("duesSection").style.display = "none";
    document.getElementById("providerSection").style.display = "none";

    document
        .querySelectorAll(".menuItem")
        .forEach((item) => item.classList.remove("active"));

    document.getElementById(sectionId).style.display = "block";
    menuItem.classList.add("active");
}

function openStudentModal() {
    document.getElementById("newStudentNumber").value = "";
    document.getElementById("newStudentName").value = "";
    document.getElementById("newStudentMajor").value = "PLATFORM";
    document.getElementById("newStudentRole").value = "ROLE_STUDENT";

    document.getElementById("studentModal").style.display = "block";
}

function openDuesModal() {
    document.getElementById("duesStudentId").value = "";
    document.getElementById("depositorName").value = "";
    document.getElementById("amount").value = "";
    document.getElementById("remainingSemesters").value = "";
    document.getElementById("submittedAt").value = "";

    document.getElementById("duesModal").style.display = "block";
}

function openProviderModal() {
    document.getElementById("newProviderEmail").value = "";
    document.getElementById("newProviderName").value = "";
    document.getElementById("newProviderKey").value = "";
    document.getElementById("newProviderStudentId").value = "";

    document.getElementById("providerModal").style.display = "block";
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = "none";
}

function toggleAllCheckboxes(tableId, masterCheckbox) {
    const table = document.getElementById(tableId);
    const checkboxes = table.querySelectorAll('tbody input[type="checkbox"]');
    checkboxes.forEach((chk) => {
        chk.checked = masterCheckbox.checked;
    });
}

async function createStudent() {
    const studentNumber = document
        .getElementById("newStudentNumber")
        .value.trim();
    const name = document.getElementById("newStudentName").value.trim();
    const major = document.getElementById("newStudentMajor").value;
    const role = document.getElementById("newStudentRole").value;

    if (!studentNumber || !name) {
        alert("학번, 이름을 모두 입력해주세요.");
        return;
    }

    try {
        const response = await fetch(`${contextPath}/manage/students`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({studentNumber, name, major, role}),
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

async function updateStudent(button) {
    const row = button.closest("tr");
    const studentId = row.getAttribute("data-student-id");
    const studentNumber = row
        .querySelector("[data-field='studentNumber']")
        .innerText.trim();
    const name = row.querySelector("[data-field='name']").innerText.trim();
    const major = row.querySelector("select[data-field='major']").value;
    const role = row.querySelector("select[data-field='role']").value;

    try {
        const response = await fetch(
            `${contextPath}/manage/students/${studentId}`,
            {
                method: "PATCH",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({studentNumber, name, major, role}),
            }
        );
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

async function deleteSelectedStudents() {
    const checkboxes = document.querySelectorAll(
        "#studentTable tbody input[type='checkbox']:checked"
    );
    if (checkboxes.length === 0) {
        alert("선택된 항목이 없습니다.");
        return;
    }
    if (!confirm("정말 삭제하시겠습니까?")) return;

    for (const chk of checkboxes) {
        const row = chk.closest("tr");
    const studentId = row.getAttribute("data-student-id");
    try {
        const res = await fetch(`${contextPath}/manage/students/${studentId}`, {
            method: "DELETE",
        });
        if (!res.ok) {
            const errorData = await res.json();
            alert(`[학생 삭제 실패]\n${errorData.msg || "에러"}`);
        } else {
            // 성공 시 해당 행 제거
        row.remove();
        }
    } catch (e) {
        console.error(e);
        alert("삭제 도중 오류가 발생했습니다.");
    }
    }
    alert("선택된 학생이 삭제되었습니다.");
}

// =========================
// Dues CRUD
// =========================
async function createDues() {
    const studentId = document.getElementById("duesStudentId").value.trim();
    const depositorName = document.getElementById("depositorName").value.trim();
    const amount = Number(document.getElementById("amount").value.trim());
    const remainingSemesters = Number(
        document.getElementById("remainingSemesters").value.trim()
    );
    const submittedAt = document.getElementById("submittedAt").value;

    if (
        !studentId ||
        !depositorName ||
        !amount ||
        !remainingSemesters ||
        !submittedAt
    ) {
        alert("필수 정보를 모두 입력해주세요.");
        return;
    }

    try {
        const response = await fetch(`${contextPath}/manage/dues`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                studentId: Number(studentId),
                depositorName,
                amount,
                remainingSemesters,
                submittedAt,
            }),
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

async function updateDues(button) {
    const row = button.closest("tr");
    const duesId = row.getAttribute("data-dues-id");
    const depositorName = row
        .querySelector("[data-field='depositorName']")
        .innerText.trim();
    const amount = row.querySelector("[data-field='amount']").innerText.trim();
    const remainingSemesters = row
        .querySelector("[data-field='remainingSemesters']")
        .innerText.trim();

    try {
        const response = await fetch(`${contextPath}/manage/dues/${duesId}`, {
            method: "PATCH",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                depositorName,
                amount: Number(amount),
                remainingSemesters: Number(remainingSemesters),
            }),
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

async function deleteSelectedDues() {
    const checkboxes = document.querySelectorAll(
        "#duesTable tbody input[type='checkbox']:checked"
    );
    if (checkboxes.length === 0) {
        alert("선택된 항목이 없습니다.");
        return;
    }
    if (!confirm("정말 삭제하시겠습니까?")) return;

    for (const chk of checkboxes) {
        const row = chk.closest("tr");
    const duesId = row.getAttribute("data-dues-id");
    try {
        const res = await fetch(`${contextPath}/manage/dues/${duesId}`, {
            method: "DELETE",
        });
        if (!res.ok) {
            const errorData = await res.json();
            alert(`[회비 납부 내역 삭제 실패]\n${errorData.msg || "에러"}`);
        } else {
        row.remove();
        }
    } catch (e) {
        console.error(e);
        alert("삭제 도중 오류가 발생했습니다.");
    }
    }
    alert("선택된 회비 납부 내역이 삭제되었습니다.");
}

// =========================
// Provider CRUD
// =========================
function openProviderModal() {
    // 모달 열 때마다 폼 초기화
    document.getElementById("newProviderEmail").value = "";
    document.getElementById("newProviderName").value = "";
    document.getElementById("newProviderKey").value = "";
    document.getElementById("newProviderStudentId").value = "";
    document.getElementById("providerModal").style.display = "block";
}

async function createProvider() {
    const email = document.getElementById("newProviderEmail").value.trim();
    const providerName = document.getElementById("newProviderName").value.trim();
    const providerKey = document.getElementById("newProviderKey").value.trim();
    const studentId = document
        .getElementById("newProviderStudentId")
        .value.trim();

    if (!email || !providerName || !providerKey) {
        alert("필수 정보(Email, Provider Name, Provider Key)를 입력해주세요.");
        return;
    }

    try {
        const response = await fetch(`${contextPath}/manage/providers`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                email,
                providerName,
                providerKey,
                studentId: studentId ? Number(studentId) : null,
            }),
        });
        if (!response.ok) {
            const errorData = await response.json();
            alert(`[Provider 추가 실패]\n${errorData.msg || "에러"}`);
            return;
        }
        alert("Provider가 추가되었습니다. 페이지를 새로고침합니다.");
        location.reload();
    } catch (e) {
        console.error(e);
        alert("추가 도중 오류가 발생했습니다.");
    }
}

async function updateProvider(button) {
    const row = button.closest("tr");
    const providerId = row.getAttribute("data-provider-id");
    const email = row.querySelector("[data-field='email']").innerText.trim();
    const providerName = row
        .querySelector("[data-field='providerName']")
        .innerText.trim();
    const providerKey = row
        .querySelector("[data-field='providerKey']")
        .innerText.trim();
    const studentIdStr = row
        .querySelector("[data-field='studentId']")
        .innerText.trim();
    const studentId = studentIdStr ? Number(studentIdStr) : null;

    try {
        const response = await fetch(
            `${contextPath}/manage/providers/${providerId}`,
            {
                method: "PATCH",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({email, providerName, providerKey, studentId}),
            }
        );
        if (!response.ok) {
            const errorData = await response.json();
            alert(`[Provider 수정 실패]\n${errorData.msg || "에러"}`);
            return;
        }
        alert("Provider 정보가 수정되었습니다.");
    } catch (e) {
        console.error(e);
        alert("수정 도중 오류가 발생했습니다.");
    }
}

async function deleteSelectedProviders() {
    const checkboxes = document.querySelectorAll(
        "#providerTable tbody input[type='checkbox']:checked"
    );
    if (checkboxes.length === 0) {
        alert("선택된 항목이 없습니다.");
        return;
    }
    if (!confirm("정말 삭제하시겠습니까?")) return;

    for (const chk of checkboxes) {
        const row = chk.closest("tr");
        const providerId = row.getAttribute("data-provider-id");
        try {
            const res = await fetch(`${contextPath}/manage/providers/${providerId}`, {
                method: "DELETE",
            });
            if (!res.ok) {
                const errorData = await res.json();
                alert(`[Provider 삭제 실패]\n${errorData.msg || "에러"}`);
            } else {
                row.remove();
            }
        } catch (e) {
            console.error(e);
            alert("삭제 도중 오류가 발생했습니다.");
        }
    }
    alert("선택된 Provider가 삭제되었습니다.");
}
