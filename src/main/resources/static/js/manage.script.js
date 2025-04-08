// context path
const contextPath = "/auth";

// Global States
let studentPage = 0,
  studentSize = 10,
  studentSortBy = "studentNumber",
  studentSortDirection = "asc",
  studentTotalPages = 0;
let duesPage = 0,
  duesSize = 10,
  duesSortBy = "duesId",
  duesSortDirection = "asc",
  duesTotalPages = 0;
let providerPage = 0,
  providerSize = 10,
  providerSortBy = "email",
  providerSortDirection = "asc",
  providerTotalPages = 0;
let qrAuthLogPage = 0,
  qrAuthLogSize = 10,
  qrAuthLogSortBy = "scanDate",
  qrAuthLogSortDirection = "desc",
  qrAuthLogTotalPages = 0;

document.addEventListener("DOMContentLoaded", () => {
  loadStudentPage();
});

function showSection(sectionId, menuItem) {
  ["studentSection", "duesSection", "providerSection", "qrAuthSection"].forEach(
    (id) => {
      document.getElementById(id).style.display = "none";
    }
  );
  document
    .querySelectorAll(".menuItem")
    .forEach((item) => item.classList.remove("active"));
  document.getElementById(sectionId).style.display = "block";
  menuItem.classList.add("active");

  if (sectionId === "studentSection") loadStudentPage();
  else if (sectionId === "duesSection") loadDuesPage();
  else if (sectionId === "providerSection") loadProviderPage();
  else if (sectionId === "qrAuthSection") loadQrAuthLogPage();
}

function toggleAllCheckboxes(tableId, masterCheckbox) {
  const checkboxes = document.querySelectorAll(
    `#${tableId} tbody input[type='checkbox']`
  );
  checkboxes.forEach((chk) => (chk.checked = masterCheckbox.checked));
}

// -------------------- Student --------------------
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
      <td>X</td>
      <td><button class="editButton" onclick="updateStudent(this)">수정</button></td>
    `;
    tbody.appendChild(tr);
  });
}

function renderStudentPagination() {
  renderPagination(
    "studentPagination",
    studentPage,
    studentTotalPages,
    (page) => {
      studentPage = page;
      loadStudentPage();
    }
  );
}

function sortStudent(field) {
  if (studentSortBy === field)
    studentSortDirection = studentSortDirection === "asc" ? "desc" : "asc";
  else {
    studentSortBy = field;
    studentSortDirection = "asc";
  }
  studentPage = 0;
  loadStudentPage();
}

// -------------------- Dues --------------------
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
    const pageData = data.data;

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
  renderPagination("duesPagination", duesPage, duesTotalPages, (page) => {
    duesPage = page;
    loadDuesPage();
  });
}

function sortDues(field) {
  if (duesSortBy === field)
    duesSortDirection = duesSortDirection === "asc" ? "desc" : "asc";
  else {
    duesSortBy = field;
    duesSortDirection = "asc";
  }
  duesPage = 0;
  loadDuesPage();
}

// -------------------- Provider --------------------
async function loadProviderPage() {
  try {
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
    const pageData = data.data;

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
  renderPagination(
    "providerPagination",
    providerPage,
    providerTotalPages,
    (page) => {
      providerPage = page;
      loadProviderPage();
    }
  );
}

function sortProvider(field) {
  if (providerSortBy === field)
    providerSortDirection = providerSortDirection === "asc" ? "desc" : "asc";
  else {
    providerSortBy = field;
    providerSortDirection = "asc";
  }
  providerPage = 0;
  loadProviderPage();
}

// -------------------- QR Auth Log --------------------
async function loadQrAuthLogPage() {
  try {
    const url = new URL(
      `${contextPath}/manage/qr-auth`,
      window.location.origin
    );
    url.searchParams.set("sortBy", qrAuthLogSortBy);
    url.searchParams.set("direction", qrAuthLogSortDirection);
    url.searchParams.set("page", qrAuthLogPage);
    url.searchParams.set("size", qrAuthLogSize);

    const res = await fetch(url);
    if (!res.ok) throw new Error("QR-Auth 로그 조회 실패");
    const data = await res.json();
    const pageData = data.data;

    renderQrAuthLogTable(pageData.content);
    qrAuthLogTotalPages = pageData.totalPages;
    renderQrAuthLogPagination();
  } catch (err) {
    console.error(err);
    alert("QR-Auth 로그 로딩 오류");
  }
}

function renderQrAuthLogTable(logs) {
  const tbody = document.querySelector("#qrAuthLogTable tbody");
  tbody.innerHTML = "";

  logs.forEach((item) => {
    const tr = document.createElement("tr");
    tr.setAttribute("data-qr-auth-log-id", item.qrAuthLogId);
    tr.innerHTML = `
      <td><input type="checkbox"></td>
      <td>${item.qrAuthLogId}</td>
      <td>${item.scanDate}</td>
      <td>${item.studentNumber}</td>
      <td>${item.studentName}</td>
      <td>${item.duesPaid ? "true" : "false"}</td>
      <td>${item.scannedBy}</td>
    `;
    tbody.appendChild(tr);
  });
}

function renderQrAuthLogPagination() {
  renderPagination(
    "qrAuthLogPagination",
    qrAuthLogPage,
    qrAuthLogTotalPages,
    (page) => {
      qrAuthLogPage = page;
      loadQrAuthLogPage();
    }
  );
}

function sortQrAuthLog(field) {
  if (qrAuthLogSortBy === field)
    qrAuthLogSortDirection = qrAuthLogSortDirection === "asc" ? "desc" : "asc";
  else {
    qrAuthLogSortBy = field;
    qrAuthLogSortDirection = "asc";
  }
  qrAuthLogPage = 0;
  loadQrAuthLogPage();
}

function renderPagination(containerId, currentPage, totalPages, onClickPage) {
  const container = document.getElementById(containerId);
  container.innerHTML = "";
  const start = Math.floor(currentPage / 5) * 5;

  if (currentPage > 0) {
    const prevBtn = document.createElement("button");
    prevBtn.textContent = "<";
    prevBtn.classList.add("pageButton");
    prevBtn.onclick = () => onClickPage(currentPage - 1);
    container.appendChild(prevBtn);
  }

  for (let i = start; i < start + 5 && i < totalPages; i++) {
    const pageBtn = document.createElement("button");
    pageBtn.textContent = i + 1;
    pageBtn.classList.add("pageButton");
    if (i === currentPage) pageBtn.classList.add("active");
    pageBtn.onclick = () => onClickPage(i);
    container.appendChild(pageBtn);
  }

  if (start + 5 < totalPages) {
    const nextBtn = document.createElement("button");
    nextBtn.textContent = ">";
    nextBtn.classList.add("pageButton");
    nextBtn.onclick = () => onClickPage(start + 5);
    container.appendChild(nextBtn);
  }
}
