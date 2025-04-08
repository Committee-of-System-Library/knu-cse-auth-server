let qrAuthLogPage = 0,
    qrAuthLogSize = 10,
    qrAuthLogSortBy = "scanDate",
    qrAuthLogSortDirection = "desc",
    qrAuthLogTotalPages = 0;

let qrAuthSearchColumn = "";
let qrAuthSearchKeyword = "";

function searchQrAuthLog() {
    qrAuthSearchColumn = document.getElementById("qrAuthSearchColumn").value; // ""이면 전체
    qrAuthSearchKeyword = document.getElementById("qrAuthSearchKeyword").value.trim();
    qrAuthLogPage = 0;
    loadQrAuthLogPage();
}

async function loadQrAuthLogPage() {
    try {
        const url = new URL(`${contextPath}/manage/qr-auth`, window.location.origin);
        url.searchParams.set("sortBy", qrAuthLogSortBy);
        url.searchParams.set("direction", qrAuthLogSortDirection);
        url.searchParams.set("page", qrAuthLogPage);
        url.searchParams.set("size", qrAuthLogSize);

        if (qrAuthSearchColumn) {
            url.searchParams.set("searchColumn", qrAuthSearchColumn);
        }
        if (qrAuthSearchKeyword) {
            url.searchParams.set("searchKeyword", qrAuthSearchKeyword);
        }

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
    renderPagination("qrAuthLogPagination", qrAuthLogPage, qrAuthLogTotalPages, (page) => {
        qrAuthLogPage = page;
        loadQrAuthLogPage();
    });
}

function sortQrAuthLog(field) {
    if (qrAuthLogSortBy === field) {
        qrAuthLogSortDirection = qrAuthLogSortDirection === "asc" ? "desc" : "asc";
    } else {
        qrAuthLogSortBy = field;
        qrAuthLogSortDirection = "asc";
    }
    qrAuthLogPage = 0;
    loadQrAuthLogPage();
}

async function deleteSelectedQrAuthLogs() {
    const checkboxes = document.querySelectorAll("#qrAuthLogTable tbody input[type='checkbox']:checked");
    if (checkboxes.length === 0) {
        alert("선택된 항목이 없습니다.");
        return;
    }
    if (!confirm("정말 삭제하시겠습니까?")) return;

    for (const chk of checkboxes) {
        const row = chk.closest("tr");
        const logId = row.getAttribute("data-qr-auth-log-id");
        try {
            const res = await fetch(`${contextPath}/manage/qr-auth/${logId}`, {
                method: "DELETE",
            });
            if (!res.ok) {
                const errorData = await res.json();
                alert(`[로그 삭제 실패]\n${errorData.msg || "에러"}`);
            } else {
                row.remove();
            }
        } catch (e) {
            console.error(e);
            alert("삭제 도중 오류 발생");
        }
    }
    alert("선택된 로그가 삭제되었습니다.");
}

// 전역 노출
window.searchQrAuthLog = searchQrAuthLog;
window.loadQrAuthLogPage = loadQrAuthLogPage;
window.sortQrAuthLog = sortQrAuthLog;
window.deleteSelectedQrAuthLogs = deleteSelectedQrAuthLogs;
