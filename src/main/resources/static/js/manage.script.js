const contextPath = "/auth";

// =========================
// 글로벌 상태 (학생)
let studentPage = 0;
let studentSize = 10;
let studentSortBy = "studentNumber";
let studentSortDirection = "asc";
let studentTotalPages = 0;

// =========================
// 글로벌 상태 (dues)
let duesPage = 0;
let duesSize = 10;
let duesSortBy = "duesId";
let duesSortDirection = "asc";
let duesTotalPages = 0;

// =========================
// 글로벌 상태 (provider)
let providerPage = 0;
let providerSize = 10;
let providerSortBy = "email";
let providerSortDirection = "asc";
let providerTotalPages = 0;

document.addEventListener("DOMContentLoaded", () => {
  // 페이지 로드 시 학생 목록 먼저 로딩
  loadStudentPage();
});

//////////////////////////////////////////////////////////
// 탭(섹션) 전환
//////////////////////////////////////////////////////////
function showSection(sectionId, menuItem) {
  // 모든 섹션 숨김
  document.getElementById("studentSection").style.display = "none";
  document.getElementById("duesSection").style.display = "none";
  document.getElementById("providerSection").style.display = "none";

  // 사이드바 active 효과 제거
  document
    .querySelectorAll(".menuItem")
    .forEach((item) => item.classList.remove("active"));

  // 해당 섹션 보이기 + active 설정
  document.getElementById(sectionId).style.display = "block";
  menuItem.classList.add("active");

  // 섹션별로 로딩 로직
  if (sectionId === "studentSection") {
    loadStudentPage();
  } else if (sectionId === "duesSection") {
    loadDuesPage();
  } else if (sectionId === "providerSection") {
    loadProviderPage();
  }
}

//////////////////////////////////////////////////////////
// 1) Student 목록 + 정렬 + 페이지네이션
//////////////////////////////////////////////////////////
async function loadStudentPage() {
  try {
    const url = new URL(
      `${contextPath}/manage/students`,
      window.location.origin
    );
    url.searchParams.set("sortBy", studentSortBy);
    url.searchParams.set("direction", studentSortDirection);
    url.searchParams.set("page", studentPage);
    url.searchParams.set("size", studentSize);

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
    tr.setAttribute("data-student-id", item.studentNumber);

    tr.innerHTML = `
      <td><input type="checkbox"></td>
      <td>${item.studentId}</td>
      <td contenteditable="true" data-field="studentNumber">${
        item.studentNumber
      }</td>
      <td contenteditable="true" data-field="name">${item.name}</td>
      <td>
        <select class="styled-dropdown" data-field="major">
          <option value="PLATFORM" ${
            item.major === "PLATFORM" ? "selected" : ""
          }>PLATFORM</option>
          <option value="AI" ${
            item.major === "AI" ? "selected" : ""
          }>AI</option>
          <option value="GLOBAL" ${
            item.major === "GLOBAL" ? "selected" : ""
          }>GLOBAL</option>
          <option value="NONE" ${
            item.major === "NONE" ? "selected" : ""
          }>NONE</option>
        </select>
      </td>
      <td>
        <select class="styled-dropdown" data-field="role">
          <option value="ROLE_STUDENT" ${
            item.role === "ROLE_STUDENT" ? "selected" : ""
          }>ROLE_STUDENT</option>
          <option value="ROLE_EXECUTIVE" ${
            item.role === "ROLE_EXECUTIVE" ? "selected" : ""
          }>ROLE_EXECUTIVE</option>
          <option value="ROLE_FINANCE" ${
            item.role === "ROLE_FINANCE" ? "selected" : ""
          }>ROLE_FINANCE</option>
          <option value="ROLE_ADMIN" ${
            item.role === "ROLE_ADMIN" ? "selected" : ""
          }>ROLE_ADMIN</option>
        </select>
      </td>
      <td>X</td>
      <td><button class="editButton" onclick="updateStudent(this)">수정</button></td>
    `;
    tbody.appendChild(tr);
  });
}

function renderStudentPagination() {
  const container = document.getElementById("studentPagination");
  container.innerHTML = "";

  const total = studentTotalPages;
  const current = studentPage;

  // 이전 버튼
  if (current > 0) {
    const prevBtn = document.createElement("button");
    prevBtn.textContent = "<";
    prevBtn.classList.add("pageButton");
    prevBtn.onclick = () => {
      studentPage = current - 1;
      loadStudentPage();
    };
    container.appendChild(prevBtn);
  }

  // 5개 단위 표시
  const start = Math.floor(current / 5) * 5;
  for (let i = start; i < start + 5 && i < total; i++) {
    const pageBtn = document.createElement("button");
    pageBtn.textContent = i + 1;
    pageBtn.classList.add("pageButton");
    if (i === current) {
      pageBtn.classList.add("active");
    }
    pageBtn.onclick = () => {
      studentPage = i;
      loadStudentPage();
    };
    container.appendChild(pageBtn);
  }

  // 다음 버튼
  if (start + 5 < total) {
    const nextBtn = document.createElement("button");
    nextBtn.textContent = ">";
    nextBtn.classList.add("pageButton");
    nextBtn.onclick = () => {
      studentPage = start + 5;
      loadStudentPage();
    };
    container.appendChild(nextBtn);
  }
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

//////////////////////////////////////////////////////////
// 2) Dues 목록
//////////////////////////////////////////////////////////
async function loadDuesPage() {
  try {
    const url = new URL(`${contextPath}/manage/dues`, window.location.origin);
    url.searchParams.set("sortBy", duesSortBy);
    url.searchParams.set("direction", duesSortDirection);
    url.searchParams.set("page", duesPage);
    url.searchParams.set("size", duesSize);

    const res = await fetch(url);
    if (!res.ok) throw new Error("회비 목록 조회 실패");
    const data = await res.json();
    const pageData = data.data; // Page<DuesListResponse>

    renderDuesTable(pageData.content);
    duesTotalPages = pageData.totalPages;
    renderDuesPagination();
  } catch (err) {
    console.error(err);
    alert("회비 목록 로딩 중 오류가 발생했습니다.");
  }
}

function renderDuesTable(duesArr) {
  const tbody = document.querySelector("#duesTable tbody");
  tbody.innerHTML = "";

  duesArr.forEach((item) => {
    const tr = document.createElement("tr");
    tr.setAttribute("data-dues-id", item.duesId);
    tr.innerHTML = `
      <td><input type="checkbox"></td>
      <td>${item.duesId}</td>
      <td>${item.studentName}</td>
      <td>${item.studentNumber}</td>
      <td contenteditable="true" data-field="depositorName">${
        item.depositorName
      }</td>
      <td contenteditable="true" data-field="amount">${item.amount}</td>
      <td contenteditable="true" data-field="remainingSemesters">${
        item.remainingSemesters
      }</td>
      <td>${item.submittedAt ? item.submittedAt.replace("T", " ") : ""}</td>
      <td><button class="editButton" onclick="updateDues(this)">수정</button></td>
    `;
    tbody.appendChild(tr);
  });
}

function renderDuesPagination() {
  const container = document.getElementById("duesPagination");
  container.innerHTML = "";

  const total = duesTotalPages;
  const current = duesPage;

  if (current > 0) {
    const prevBtn = document.createElement("button");
    prevBtn.textContent = "<";
    prevBtn.classList.add("pageButton");
    prevBtn.onclick = () => {
      duesPage = current - 1;
      loadDuesPage();
    };
    container.appendChild(prevBtn);
  }

  const start = Math.floor(current / 5) * 5;
  for (let i = start; i < start + 5 && i < total; i++) {
    const pageBtn = document.createElement("button");
    pageBtn.textContent = i + 1;
    pageBtn.classList.add("pageButton");
    if (i === current) {
      pageBtn.classList.add("active");
    }
    pageBtn.onclick = () => {
      duesPage = i;
      loadDuesPage();
    };
    container.appendChild(pageBtn);
  }

  if (start + 5 < total) {
    const nextBtn = document.createElement("button");
    nextBtn.textContent = ">";
    nextBtn.classList.add("pageButton");
    nextBtn.onclick = () => {
      duesPage = start + 5;
      loadDuesPage();
    };
    container.appendChild(nextBtn);
  }
}

function sortDues(field) {
  if (duesSortBy === field) {
    duesSortDirection = duesSortDirection === "asc" ? "desc" : "asc";
  } else {
    duesSortBy = field;
    duesSortDirection = "asc";
  }
  duesPage = 0;
  loadDuesPage();
}

//////////////////////////////////////////////////////////
// 3) Provider 목록
//////////////////////////////////////////////////////////
async function loadProviderPage() {
  try {
    // GET /providers?sortBy=...&direction=...&page=...
    const url = new URL(
      `${contextPath}/manage/providers`,
      window.location.origin
    );
    url.searchParams.set("sortBy", providerSortBy);
    url.searchParams.set("direction", providerSortDirection);
    url.searchParams.set("page", providerPage);
    url.searchParams.set("size", providerSize);

    const res = await fetch(url);
    if (!res.ok) throw new Error("Provider 목록 조회 실패");
    const data = await res.json();
    const pageData = data.data; // Page<ProviderResponse>

    renderProviderTable(pageData.content);
    providerTotalPages = pageData.totalPages;
    renderProviderPagination();
  } catch (err) {
    console.error(err);
    alert("Provider 목록 로딩 중 오류가 발생했습니다.");
  }
}

function renderProviderTable(arr) {
  const tbody = document.querySelector("#providerTable tbody");
  tbody.innerHTML = "";

  arr.forEach((item) => {
    const tr = document.createElement("tr");
    tr.setAttribute("data-provider-id", item.id ?? "");
    tr.innerHTML = `
      <td><input type="checkbox"></td>
      <td>${item.id ?? ""}</td>
      <td contenteditable="true" data-field="email">${item.email}</td>
      <td contenteditable="true" data-field="providerName">${
        item.providerName
      }</td>
      <td contenteditable="true" data-field="providerKey">${
        item.providerKey
      }</td>
      <td contenteditable="true" data-field="studentId">${
        item.studentId ?? ""
      }</td>
      <td><button class="editButton" onclick="updateProvider(this)">수정</button></td>
    `;
    tbody.appendChild(tr);
  });
}

function renderProviderPagination() {
  const container = document.getElementById("providerPagination");
  container.innerHTML = "";

  const total = providerTotalPages;
  const current = providerPage;

  if (current > 0) {
    const prevBtn = document.createElement("button");
    prevBtn.textContent = "<";
    prevBtn.classList.add("pageButton");
    prevBtn.onclick = () => {
      providerPage = current - 1;
      loadProviderPage();
    };
    container.appendChild(prevBtn);
  }

  const start = Math.floor(current / 5) * 5;
  for (let i = start; i < start + 5 && i < total; i++) {
    const pageBtn = document.createElement("button");
    pageBtn.textContent = i + 1;
    pageBtn.classList.add("pageButton");
    if (i === current) {
      pageBtn.classList.add("active");
    }
    pageBtn.onclick = () => {
      providerPage = i;
      loadProviderPage();
    };
    container.appendChild(pageBtn);
  }

  if (start + 5 < total) {
    const nextBtn = document.createElement("button");
    nextBtn.textContent = ">";
    nextBtn.classList.add("pageButton");
    nextBtn.onclick = () => {
      providerPage = start + 5;
      loadProviderPage();
    };
    container.appendChild(nextBtn);
  }
}

function sortProvider(field) {
  if (providerSortBy === field) {
    providerSortDirection = providerSortDirection === "asc" ? "desc" : "asc";
  } else {
    providerSortBy = field;
    providerSortDirection = "asc";
  }
  providerPage = 0;
  loadProviderPage().then((r) => console.log(r));
}

//////////////////////////////////////////////////////////
// 공통 함수
//////////////////////////////////////////////////////////
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

//////////////////////////////////////////////////////////
// 아래는 CREATE/UPDATE/DELETE 로직 (기존과 동일)
//////////////////////////////////////////////////////////

// ========== Student ==========
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
    const response = await fetch(`${contextPath}/manage/students`, {
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
  const studentId = row.getAttribute("data-student-id"); // 실제 ID 필요

  const studentNumber = row
    .querySelector("[data-field='studentNumber']")
    .innerText.trim();
  const name = row.querySelector("[data-field='name']").innerText.trim();
  const major = row.querySelector("[data-field='major']").value;
  const role = row.querySelector("[data-field='role']").value;

  try {
    const response = await fetch(
      `${contextPath}/manage/students/${studentId}`,
      {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ studentNumber, name, major, role }),
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
        row.remove();
      }
    } catch (e) {
      console.error(e);
      alert("삭제 도중 오류가 발생했습니다.");
    }
  }
  alert("선택된 학생이 삭제되었습니다.");
}

// ========== Dues ==========
function openDuesModal() {
  document.getElementById("duesStudentId").value = "";
  document.getElementById("depositorName").value = "";
  document.getElementById("amount").value = "";
  document.getElementById("remainingSemesters").value = "";
  document.getElementById("submittedAt").value = "";

  document.getElementById("duesModal").style.display = "block";
}

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
      headers: { "Content-Type": "application/json" },
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
    alert("회비 납부 정보가 추가되었습니다.");
    closeModal("duesModal");
    loadDuesPage();
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
      headers: { "Content-Type": "application/json" },
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

// ========== Provider ==========
function openProviderModal() {
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
      headers: { "Content-Type": "application/json" },
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
    alert("Provider가 추가되었습니다.");
    closeModal("providerModal");
    loadProviderPage();
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
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, providerName, providerKey, studentId }),
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
