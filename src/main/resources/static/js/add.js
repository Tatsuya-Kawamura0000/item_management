document.addEventListener("DOMContentLoaded", () => {

    // レシート関連要素
    const receiptMethodButton = document.getElementById("receiptMethodButton");
    const receiptModal = document.getElementById("receiptModal");
    const closeReceiptModal = document.getElementById("closeReceiptModal");
    const manualFromModalBtn = document.getElementById("manualFromModal");

    // 手動追加関連要素
    const manualBtn = document.getElementById("manualMethodButton");
    const manualModal = document.getElementById("manualAddModal");
    const closeManual = document.getElementById("closeManualModal");
    const cancelManual = document.getElementById("cancelManualAdd");
    const manualAddButton = document.getElementById("manualAddButton");
    const manualNameInput = document.getElementById("manualFoodName");
    const manualQuantityInput = document.getElementById("manualFoodQuantity");
    const manualUnitSelect = document.getElementById("manualFoodUnit");
    const manualCategorySelect = document.getElementById("manualFoodCategory");
    const manualDeadlineInput = document.getElementById("manualFoodDeadline");
    const manualSuggestions = document.getElementById("manualSuggestions");

    // カメラ関連要素
    const startCameraBtn = document.getElementById("startCameraButton");
    const cameraModal = document.getElementById("cameraModal");
    const closeCameraModal = document.getElementById("closeCameraModal");
    const cancelCameraBtn = document.getElementById("cancelCameraButton");
    const cameraVideo = document.getElementById("cameraVideo");
    let cameraStream = null;

    // レシートモーダル開閉
    if (receiptMethodButton && receiptModal) {
        receiptMethodButton.addEventListener("click", () => receiptModal.classList.add("show"));
    }
    if (closeReceiptModal && receiptModal) {
        closeReceiptModal.addEventListener("click", () => receiptModal.classList.remove("show"));
    }

    // レシートモーダル内の「手動で追加する」ボタン
    if (manualFromModalBtn && receiptModal && manualModal) {
        manualFromModalBtn.addEventListener("click", () => {
            receiptModal.classList.remove("show");
            manualModal.classList.add("show");
        });
    }

    // 手動追加モーダル開閉
    if (manualBtn && manualModal) {
        manualBtn.addEventListener("click", () => manualModal.classList.add("show"));
    }
    if (closeManual) closeManual.addEventListener("click", () => manualModal.classList.remove("show"));
    if (cancelManual) cancelManual.addEventListener("click", () => manualModal.classList.remove("show"));

    /**
     * カメラを起動してビデオ要素に映像をプレビュー表示
     */
    async function startCamera() {
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
            alert("お使いのブラウザまたは環境はカメラ機能に対応していません。HTTPS接続または対応ブラウザをご確認ください。");
            return;
        }

        try {
            // 背面カメラ（environment）を優先して取得
            try {
                cameraStream = await navigator.mediaDevices.getUserMedia({
                    video: { facingMode: "environment" },
                    audio: false
                });
            } catch (fallbackErr) {
                // 背面カメラ指定で失敗した場合は制約なしで再試行
                console.warn("FacingMode environment failed, trying fallback:", fallbackErr);
                cameraStream = await navigator.mediaDevices.getUserMedia({
                    video: true,
                    audio: false
                });
            }

            if (cameraVideo) {
                cameraVideo.srcObject = cameraStream;
                await cameraVideo.play();
            }

            // レシート選択モーダルを閉じ、カメラプレビューモーダルを開く
            if (receiptModal) receiptModal.classList.remove("show");
            if (cameraModal) cameraModal.classList.add("show");

        } catch (err) {
            console.error("Camera access error:", err);
            if (err.name === "NotAllowedError" || err.name === "PermissionDeniedError") {
                alert("カメラへのアクセスが許可されていません。\n端末やブラウザの設定でカメラへのアクセスを許可してください。");
            } else if (err.name === "NotFoundError" || err.name === "DevicesNotFoundError") {
                alert("利用可能なカメラが見つかりませんでした。");
            } else if (err.name === "NotReadableError" || err.name === "TrackStartError") {
                alert("カメラが他のアプリケーションで使用中か、起動できませんでした。");
            } else if (err.name === "OverconstrainedError") {
                alert("要求された条件に適合するカメラが見つかりませんでした。");
            } else {
                alert("カメラの起動に失敗しました: " + (err.message || err.name));
            }
        }
    }

    /**
     * カメラを停止してプレビューモーダルを閉じる
     */
    function stopCamera() {
        if (cameraStream) {
            cameraStream.getTracks().forEach(track => {
                track.stop();
            });
            cameraStream = null;
        }
        if (cameraVideo) {
            cameraVideo.srcObject = null;
        }
        if (cameraModal) {
            cameraModal.classList.remove("show");
        }
    }

    if (startCameraBtn) {
        startCameraBtn.addEventListener("click", startCamera);
    }
    if (closeCameraModal) {
        closeCameraModal.addEventListener("click", stopCamera);
    }
    if (cancelCameraBtn) {
        cancelCameraBtn.addEventListener("click", stopCamera);
    }

    // デバウンス
    function debounce(fn, ms) {
        let t;
        return function (...args) {
            clearTimeout(t);
            t = setTimeout(() => fn.apply(this, args), ms);
        };
    }

    function parsePurchaseAmount(value) {
        if (!value) return { amount: 1, unit: "" };
        const match = String(value).match(/^(\d+(?:\.\d+)?)(.*)$/);
        if (!match) return { amount: 1, unit: "" };
        return { amount: Number(match[1]), unit: match[2] };
    }

    // サジェスト取得
    async function fetchSuggestions(keyword) {
        try {
            const url = `/api/items/suggest?keyword=${encodeURIComponent(keyword)}`;
            const resp = await fetch(url);
            if (!resp.ok) return [];
            const data = await resp.json();
            return Array.isArray(data) ? data : [];
        } catch (e) {
            console.error(e);
            return [];
        }
    }

    function renderSuggestions(items) {
        manualSuggestions.innerHTML = "";
        if (!items || items.length === 0) {
            manualSuggestions.style.display = "none";
            return;
        }
        manualSuggestions.style.display = "block";
        items.forEach(it => {
            const div = document.createElement("div");
            div.className = "suggestion-item";
            // show name and purchaseAmount (if any)
            const pa = it.purchaseAmount ? (` — ${it.purchaseAmount}`) : "";
            div.textContent = `${it.name}${pa}`;
            div.dataset.itemId = it.id;
            div.dataset.name = it.name;
            div.dataset.purchaseAmount = it.purchaseAmount || "";
            div.dataset.categoryId = it.categoryId || "";
            div.addEventListener("click", () => {
                // 選択時にフォームを埋める
                manualNameInput.value = div.dataset.name || "";
                const parsed = parsePurchaseAmount(div.dataset.purchaseAmount || "");
                manualQuantityInput.value = parsed.amount || 1;
                if (parsed.unit) manualUnitSelect.value = parsed.unit;
                if (div.dataset.categoryId) manualCategorySelect.value = div.dataset.categoryId;
                manualSuggestions.innerHTML = "";
                manualSuggestions.style.display = "none";
            });
            manualSuggestions.appendChild(div);
        });
    }

    const handleInput = debounce(async (e) => {
        const v = e.target.value.trim();
        if (!v) {
            manualSuggestions.innerHTML = "";
            manualSuggestions.style.display = "none";
            return;
        }
        const items = await fetchSuggestions(v);
        renderSuggestions(items);
    }, 250);

    if (manualNameInput) manualNameInput.addEventListener("input", handleInput);

    // 追加処理（API 経由で食材一覧へ登録）
    if (manualAddButton) {
        manualAddButton.addEventListener("click", async () => {
            const name = manualNameInput.value.trim();
            const qty = manualQuantityInput.value || "1";
            const unit = manualUnitSelect.value || "";
            const categoryId = manualCategorySelect.value || null;
            const deadline = manualDeadlineInput.value || null;

            if (!name) { alert('食材名を入力してください。'); manualNameInput.focus(); return; }
            if (!qty || Number(qty) <= 0) { alert('1以上の量を入力してください。'); manualQuantityInput.focus(); return; }

            const form = {
                name: name,
                categoryId: categoryId ? Number(categoryId) : null,
                amount: `${qty}${unit}`,
                deadline: deadline || null
            };

            try {
                const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
                const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
                const headers = { 'Content-Type': 'application/json' };
                if (token && header) headers[header] = token;

                const resp = await fetch('/api/items/create', {
                    method: 'POST',
                    headers: headers,
                    body: JSON.stringify(form)
                });
                if (!resp.ok) throw new Error('登録に失敗しました');
                const saved = await resp.json();
                alert('食材を追加しました。');
                // クリアしてモーダルは開いたまま（連続追加できるように）
                manualNameInput.value = '';
                manualQuantityInput.value = '1';
                manualUnitSelect.value = '';
                manualDeadlineInput.value = '';
                manualSuggestions.innerHTML = '';
                manualSuggestions.style.display = 'none';
                manualNameInput.focus();
                // 遷移する場合は以下を有効にする（例: 食材一覧へ）
                // window.location.href = '/foods';
            } catch (err) {
                console.error(err);
                alert('食材の登録に失敗しました。');
            }
        });
    }

});