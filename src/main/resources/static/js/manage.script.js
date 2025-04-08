let contextPath = "/auth";

function showSection(sectionId, menuItem) {
  const sections = [
    "studentSection",
    "duesSection",
    "providerSection",
    "qrAuthSection",
  ];
  sections.forEach((id) => {
    document.getElementById(id).style.display = "none";
  });

  document
      .querySelectorAll(".menuItem")
      .forEach((item) => item.classList.remove("active"));

  document.getElementById(sectionId).style.display = "block";
  menuItem.classList.add("active");

  switch (sectionId) {
    case "studentSection":
      loadStudentPage();
      break;
    case "duesSection":
      loadDuesPage();
      break;
    case "providerSection":
      loadProviderPage();
      break;
    case "qrAuthSection":
      loadQrAuthLogPage();
      break;
    default:
      break;
  }
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

function toggleAllCheckboxes(tableId, masterCheckbox) {
  const table = document.getElementById(tableId);
  const checkboxes = table.querySelectorAll("tbody input[type='checkbox']");
  checkboxes.forEach((chk) => {
    chk.checked = masterCheckbox.checked;
  });
}

window.showSection = showSection;
window.renderPagination = renderPagination;
window.toggleAllCheckboxes = toggleAllCheckboxes;
