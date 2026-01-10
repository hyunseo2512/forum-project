console.log("boardComment.js in");

// [1] 댓글 등록 버튼 (로그인 유저 전용)
const cmtAddBtn = document.getElementById("cmtAddBtn");
if (cmtAddBtn) {
    cmtAddBtn.addEventListener("click", () => {
        const cmtText = document.getElementById("cmtText");
        const cmtWriter = document.getElementById("cmtWriter");

        if (!cmtText.value) {
            alert("댓글 내용을 입력하세요.");
            cmtText.focus();
            return;
        }

        let cmtData = {
            bno: bnoValue,
            writer: cmtWriter.innerText,
            content: cmtText.value,
        };

        postCommentToServer(cmtData).then((result) => {
            if (result === "1") {
                alert("댓글이 등록되었습니다.");
                cmtText.value = "";
                spreadCommentList(bnoValue);
            }
        });
    });
}

// [2] 댓글 리스트 출력 함수
function spreadCommentList(bno) {
    commentListFromServer(bno).then((result) => {
        const ul = document.getElementById("cmtListArea");

        // 서버에서 반환한 형식이 리스트 자체인 경우를 대비해 result.length 체크
        if (result && result.length > 0) {
            ul.innerHTML = ""; // 기존 리스트 초기화
            let li = "";
            for (let comment of result) {
                li += `<li class="list-group-item border-0 border-bottom py-3" data-cno="${comment.cno}">`;
                li += `  <div class="d-flex justify-content-between align-items-start">`;
                li += `    <div class="ms-2 me-auto">`;
                li += `      <div class="fw-bold mb-1">${comment.writer}</div>`;
                li += `      <span class="cmt-content-text">${comment.content}</span>`;
                li += `    </div>`;
                li += `    <div class="text-end">`;
                li += `      <small class="text-muted d-block mb-2">${comment.regDate}</small>`;

                // [로그인 유저 권한 체크] 작성자와 현재 로그인 유저가 같을 때만 버튼 노출
                if (typeof currentUser !== 'undefined' && currentUser === comment.writer) {
                    li += `      <button type="button" class="btn btn-sm btn-outline-warning mod" data-bs-toggle="modal" data-bs-target="#commentModal">수정</button>`;
                    li += `      <button type="button" class="btn btn-sm btn-outline-danger del">삭제</button>`;
                }

                li += `    </div>`;
                li += `  </div>`;
                li += `</li>`;
            }
            ul.innerHTML = li;
        } else {
            ul.innerHTML = `<li class="list-group-item text-muted text-center py-4">등록된 댓글이 없습니다.</li>`;
        }
    });
}

// [3] 수정/삭제 이벤트 위임
document.addEventListener("click", (e) => {
    // 수정 버튼 클릭 시 모달창에 데이터 채우기
    if (e.target.classList.contains("mod")) {
        const li = e.target.closest("li");
        const cno = li.dataset.cno;
        const writer = li.querySelector(".fw-bold").innerText;
        const content = li.querySelector(".cmt-content-text").innerText;

        document.getElementById("cmtTextMod").value = content;
        document.getElementById("cmtModBtn").setAttribute("data-cno", cno);
        // 모달 헤더나 어딘가에 작성자 표시가 필요하다면 추가 가능
    }

    // 모달 내 수정 완료 버튼
    if (e.target.id === "cmtModBtn") {
        const modData = {
            cno: e.target.dataset.cno,
            content: document.getElementById("cmtTextMod").value
        };
        updateCommentToServer(modData).then(result => {
            if (result === "1") {
                alert("댓글이 수정되었습니다.");
                document.querySelector(".btn-close").click();
                spreadCommentList(bnoValue);
            }
        });
    }

    // 삭제 버튼
    if (e.target.classList.contains("del")) {
        if (!confirm("댓글을 삭제하시겠습니까?")) return;
        const li = e.target.closest("li");
        removeCommentToServer(li.dataset.cno).then(result => {
            if (result === "1") {
                alert("댓글이 삭제되었습니다.");
                spreadCommentList(bnoValue);
            }
        });
    }
});

// [4] 서버 통신 함수들
async function postCommentToServer(cmtData) {
    try {
        const resp = await fetch("/comment/post", {
            method: "post",
            headers: { "content-type": "application/json; charset=utf-8" },
            body: JSON.stringify(cmtData)
        });
        return await resp.text();
    } catch (err) { console.error(err); }
}

async function commentListFromServer(bno) {
    try {
        const resp = await fetch("/comment/list/" + bno);
        return await resp.json();
    } catch (err) { console.error(err); }
}

async function updateCommentToServer(modData) {
    try {
        const resp = await fetch("/comment/modify", {
            method: "put",
            headers: { "content-type": "application/json; charset=utf-8" },
            body: JSON.stringify(modData)
        });
        return await resp.text();
    } catch (err) { console.error(err); }
}

async function removeCommentToServer(cno) {
    try {
        const resp = await fetch("/comment/remove/" + cno, { method: "delete" });
        return await resp.text();
    } catch (err) { console.error(err); }
}

// 초기 로딩
document.addEventListener('DOMContentLoaded', () => {
    spreadCommentList(bnoValue);
});