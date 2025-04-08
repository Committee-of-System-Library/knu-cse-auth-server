let contextPath = "/auth";

// 공통: 전체 섹션을 숨기고 특정 섹션만 보이도록 하는 함수
function showSection(sectionId, menuItem) {
  // 숨길 섹션들
  const sections = ["studentSection", "duesSection", "providerSection", "qrAuthSection"];
  sections.forEach(id => {
    document.getElementById(id).style.display = "none";
  });

  // 사이드바 active 클래스 제거
  document.querySelectorAll(".menuItem").forEach(item => item.classList.remove("active"));

  // 보여줄 섹션, 활성화할 메뉴
  document.getElementById(sectionId).style.display = "block";
  menuItem.classList.add("active");
}

// 공통: 페이징 렌더링
function renderPagination(containerId, currentPage, totalPages, onClickPage) {
  const container = document.getElementById(containerId);
  container.innerHTML = "";

  // 5개씩 페이지 버튼
  const start = Math.floor(currentPage / 5) * 5;

  // 이전
  if (currentPage > 0) {
    const prevBtn = document.createElement("button");
    prevBtn.textContent = "<";
    prevBtn.classList.add("pageButton");
    prevBtn.onclick = () => onClickPage(currentPage - 1);
    container.appendChild(prevBtn);
  }

  // 중간 5개
  for (let i = start; i < start + 5 && i < totalPages; i++) {
    const pageBtn = document.createElement("button");
    pageBtn.textContent = i + 1;
    pageBtn.classList.add("pageButton");
    if (i === currentPage) pageBtn.classList.add("active");
    pageBtn.onclick = () => onClickPage(i);
    container.appendChild(pageBtn);
  }

  // 다음
  if (start + 5 < totalPages) {
    const nextBtn = document.createElement("button");
    nextBtn.textContent = ">";
    nextBtn.classList.add("pageButton");
    nextBtn.onclick = () => onClickPage(start + 5);
    container.appendChild(nextBtn);
  }
}

// 공통: 테이블에서 '전체 체크박스' 클릭 시 모든 체크박스 토글
function toggleAllCheckboxes(tableId, masterCheckbox) {
  const table = document.getElementById(tableId);
  const checkboxes = table.querySelectorAll("tbody input[type='checkbox']");
  checkboxes.forEach(chk => {
    chk.checked = masterCheckbox.checked;
  });
}

// 전역 노출
window.showSection = showSection;
window.renderPagination = renderPagination;
window.toggleAllCheckboxes = toggleAllCheckboxes;
