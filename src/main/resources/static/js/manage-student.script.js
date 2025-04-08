let studentPage = 0,
    studentSize = 10,
    studentSortBy = "id",
    studentSortDirection = "asc",
    studentTotalPages = 0;

let studentSearchColumn = "";
let studentSearchKeyword = "";

function searchStudent() {
  studentSearchColumn = document.getElementById("studentSearchColumn").value;
  studentSearchKeyword = document.getElementById("studentSearchKeyword").value.trim();
  studentPage = 0;
  loadStudentPage();
}

async function loadStudentPage() {
  try {
    const url = new URL(`/auth/manage/students`, window.location.origin);
    url.searchParams.set("sortBy", studentSortBy);
    url.searchParams.set("direction", studentSortDirection);
    url.searchParams.set("page", studentPage);
    url.searchParams.set("size", studentSize);

    if (studentSearchColumn) {
      url.searchParams.set("searchColumn", studentSearchColumn);
    }
    if (studentSearchKeyword) {
      url.searchParams.set("searchKeyword", studentSearchKeyword);
    }

    const res = await fetch(url);
    if (!res.ok) throw new Error("학생 목록 조회 실패");
    const data = await res.json();
    const pageData = data.data;

    renderStudentTable(pageData.content);
    studentTotalPages = pageData.totalPages;
    renderStudentPagination();
  } catch (err) {
    console.error(err);
    alert("학생 목록 로딩 중 오류가 발생했습니다.");
  }
}

function renderStudentTable(students) {
  const tbody = document.querySelector("#studentTable tbody");
  tbody.innerHTML = "";

  students.forEach((item) => {
    const tr = document.createElement("tr");
    tr.setAttribute("data-student-id", item.studentId);

    const paidMark = item.hasDues ? "O" : "X";

    tr.innerHTML = `
      <td><input type="checkbox"></td>
      <td>${item.studentId}</td>
      <td contenteditable="true" data-field="studentNumber">${item.studentNumber}</td>
      <td contenteditable="true" data-field="name">${item.name}</td>
      <td>
        <select class="styled-dropdown" data-field="major">
          ${["PLATFORM", "AI", "GLOBAL", "NONE"]
        .map(
            (m) =>
                `<option value="${m}" ${
                    item.major === m ? "selected" : ""
                }>${m}</option>`
        )
        .join("")}
        </select>
      </td>
      <td>
        <select class="styled-dropdown" data-field="role">
          ${["ROLE_STUDENT", "ROLE_EXECUTIVE", "ROLE_FINANCE", "ROLE_ADMIN"]
        .map(
            (r) =>
                `<option value="${r}" ${
                    item.role === r ? "selected" : ""
                }>${r}</option>`
        )
        .join("")}
        </select>
      </td>
      <td>${paidMark}</td>
      <td><button class="editButton" onclick="updateStudent(this)">수정</button></td>
    `;
    tbody.appendChild(tr);
  });
}

function renderStudentPagination() {
  renderPagination("studentPagination", studentPage, studentTotalPages, (page) => {
    studentPage = page;
    loadStudentPage();
  });
}

function sortStudent(field) {
  if (studentSortBy === field) {
    studentSortDirection = studentSortDirection === "asc" ? "desc" : "asc";
  } else {
    studentSortBy = field;
    studentSortDirection = "asc";
  }
  studentPage = 0;
  loadStudentPage();
}

function openStudentModal() {
  document.getElementById("newStudentNumber").value = "";
  document.getElementById("newStudentName").value = "";
  document.getElementById("newStudentMajor").value = "PLATFORM";
  document.getElementById("newStudentRole").value = "ROLE_STUDENT";

  document.getElementById("studentModal").style.display = "block";
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
    const response = await fetch(`/auth/manage/students`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ studentNumber, name, major, role }),
    });
    if (!response.ok) {
      const errorData = await response.json();
      alert(`[학생 추가 실패]\n${errorData.msg || "에러"}`);
      return;
    }
    alert("학생이 추가되었습니다.");
    closeModal("studentModal");
    loadStudentPage();
  } catch (e) {
    console.error(e);
    alert("추가 도중 오류가 발생했습니다.");
  }
}

async function updateStudent(button) {
  const row = button.closest("tr");
  const studentId = row.getAttribute("data-student-id");

  const studentNumber = row.querySelector("[data-field='studentNumber']").innerText.trim();
  const name = row.querySelector("[data-field='name']").innerText.trim();
  const major = row.querySelector("[data-field='major']").value;
  const role = row.querySelector("[data-field='role']").value;

  try {
    const response = await fetch(`/auth/manage/students/${studentId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ studentNumber, name, major, role }),
    });
    if (!response.ok) {
      const errorData = await response.json();
      alert(`[학생 수정 실패]\n${errorData.msg || "에러"}`);
      return;
    }
    alert("학생 정보가 수정되었습니다.");
    loadStudentPage();
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
      const res = await fetch(`/auth/manage/students/${studentId}`, {
        method: "DELETE",
      });
      if (!res.ok) {
        const errorData = await res.json();
        alert(`[학생 삭제 실패]\n${errorData.msg || "에러"}`);
      } else {
        row.remove();
      }
    } catch (e) {
      console.error(e);
      alert("삭제 도중 오류가 발생했습니다.");
    }
  }
  alert("선택된 학생이 삭제되었습니다.");
  loadStudentPage();
}

window.searchStudent = searchStudent;
window.loadStudentPage = loadStudentPage;
window.sortStudent = sortStudent;
window.openStudentModal = openStudentModal;
window.createStudent = createStudent;
window.updateStudent = updateStudent;
window.deleteSelectedStudents = deleteSelectedStudents;
