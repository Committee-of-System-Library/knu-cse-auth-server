let duesPage = 0,
    duesSize = 10,
    duesSortBy = "duesId",
    duesSortDirection = "asc",
    duesTotalPages = 0;

let duesSearchColumn = "";
let duesSearchKeyword = "";

function searchDues() {
  duesSearchColumn = document.getElementById("duesSearchColumn").value;
  duesSearchKeyword = document.getElementById("duesSearchKeyword").value.trim();
  duesPage = 0;
  loadDuesPage();
}

async function loadDuesPage() {
  try {
    const url = new URL(`${contextPath}/manage/dues`, window.location.origin);
    url.searchParams.set("sortBy", duesSortBy);
    url.searchParams.set("direction", duesSortDirection);
    url.searchParams.set("page", duesPage);
    url.searchParams.set("size", duesSize);

    if (duesSearchColumn) {
      url.searchParams.set("searchColumn", duesSearchColumn);
    }
    if (duesSearchKeyword) {
      url.searchParams.set("searchKeyword", duesSearchKeyword);
    }

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
      <td contenteditable="true" data-field="depositorName">${item.depositorName}</td>
      <td contenteditable="true" data-field="amount">${item.amount}</td>
      <td contenteditable="true" data-field="remainingSemesters">${item.remainingSemesters}</td>
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
  if (duesSortBy === field) {
    duesSortDirection = duesSortDirection === "asc" ? "desc" : "asc";
  } else {
    duesSortBy = field;
    duesSortDirection = "asc";
  }
  duesPage = 0;
  loadDuesPage();
}

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
  const remainingSemesters = Number(document.getElementById("remainingSemesters").value.trim());
  const submittedAt = document.getElementById("submittedAt").value;

  if (!studentId || !depositorName || !amount || !remainingSemesters || !submittedAt) {
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
  const depositorName = row.querySelector("[data-field='depositorName']").innerText.trim();
  const amount = Number(row.querySelector("[data-field='amount']").innerText.trim());
  const remainingSemesters = Number(row.querySelector("[data-field='remainingSemesters']").innerText.trim());

  try {
    const response = await fetch(`${contextPath}/manage/dues/${duesId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        depositorName,
        amount,
        remainingSemesters,
      }),
    });
    if (!response.ok) {
      const errorData = await response.json();
      alert(`[회비 납부 내역 수정 실패]\n${errorData.msg || "에러"}`);
      return;
    }
    alert("회비 납부 정보가 수정되었습니다.");
    loadDuesPage();
  } catch (e) {
    console.error(e);
    alert("수정 도중 오류가 발생했습니다.");
  }
}

async function deleteSelectedDues() {
  const checkboxes = document.querySelectorAll("#duesTable tbody input[type='checkbox']:checked");
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
  loadDuesPage();
}

window.searchDues = searchDues;
window.loadDuesPage = loadDuesPage;
window.sortDues = sortDues;
window.openDuesModal = openDuesModal;
window.createDues = createDues;
window.updateDues = updateDues;
window.deleteSelectedDues = deleteSelectedDues;
