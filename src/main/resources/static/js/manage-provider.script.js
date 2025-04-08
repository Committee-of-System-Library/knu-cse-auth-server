let providerPage = 0,
  providerSize = 10,
  providerSortBy = "email",
  providerSortDirection = "asc",
  providerTotalPages = 0;

let providerSearchColumn = "";
let providerSearchKeyword = "";

function searchProvider() {
  providerSearchColumn = document.getElementById("providerSearchColumn").value;
  providerSearchKeyword = document
    .getElementById("providerSearchKeyword")
    .value.trim();
  providerPage = 0;
  loadProviderPage();
}

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

    if (providerSearchColumn) {
      url.searchParams.set("searchColumn", providerSearchColumn);
    }
    if (providerSearchKeyword) {
      url.searchParams.set("searchKeyword", providerSearchKeyword);
    }

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
  if (providerSortBy === field) {
    providerSortDirection = providerSortDirection === "asc" ? "desc" : "asc";
  } else {
    providerSortBy = field;
    providerSortDirection = "asc";
  }
  providerPage = 0;
  loadProviderPage();
}

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

window.searchProvider = searchProvider;
window.loadProviderPage = loadProviderPage;
window.sortProvider = sortProvider;
window.openProviderModal = openProviderModal;
window.createProvider = createProvider;
window.updateProvider = updateProvider;
window.deleteSelectedProviders = deleteSelectedProviders;
