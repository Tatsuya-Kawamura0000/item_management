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
    const captureReceiptButton = document.getElementById("captureReceiptButton");
    const receiptPreviewModal = document.getElementById("receiptPreviewModal");
    const receiptPreviewImage = document.getElementById("receiptPreviewImage");
    const closeReceiptPreview = document.getElementById("closeReceiptPreview");
    const retakeReceiptButton = document.getElementById("retakeReceiptButton");
    const readReceiptButton = document.getElementById("readReceiptButton");
    const receiptItemsModal = document.getElementById("receiptItemsModal");
    const receiptItemsList = document.getElementById("receiptItemsList");
    const receiptReadMessage = document.getElementById("receiptReadMessage");
    const closeReceiptItemsModal = document.getElementById("closeReceiptItemsModal");
    const cancelReceiptItems = document.getElementById("cancelReceiptItems");
    const bulkAddReceiptItems = document.getElementById("bulkAddReceiptItems");
    let cameraStream = null;
    let receiptImageBlob = null;

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

    function closeReceiptPreviewModal() {
        receiptPreviewModal?.classList.remove("show");
        receiptImageBlob = null;
        if (receiptPreviewImage) receiptPreviewImage.src = "";
    }

    function captureReceipt() {
        if (!cameraVideo || !cameraVideo.videoWidth) {
            alert("カメラ映像の準備ができていません。もう一度お試しください。");
            return;
        }
        const canvas = document.createElement("canvas");
        canvas.width = cameraVideo.videoWidth;
        canvas.height = cameraVideo.videoHeight;
        canvas.getContext("2d").drawImage(cameraVideo, 0, 0, canvas.width, canvas.height);
        canvas.toBlob(blob => {
            if (!blob) {
                alert("撮影に失敗しました。もう一度お試しください。");
                return;
            }
            receiptImageBlob = blob;
            if (receiptPreviewImage) receiptPreviewImage.src = URL.createObjectURL(blob);
            stopCamera();
            receiptPreviewModal?.classList.add("show");
        }, "image/jpeg", 0.9);
    }

    captureReceiptButton?.addEventListener("click", captureReceipt);
    closeReceiptPreview?.addEventListener("click", closeReceiptPreviewModal);
    retakeReceiptButton?.addEventListener("click", () => {
        closeReceiptPreviewModal();
        startCamera();
    });

    function today() {
        return new Date().toISOString().slice(0, 10);
    }

    function validDate(value) {
        return /^\d{4}-\d{2}-\d{2}$/.test(value || "") && !Number.isNaN(new Date(`${value}T00:00:00`).getTime());
    }

    function categoryOptions(selectedId) {
        const select = document.createElement("select");
        const unknown = new Option("-", "");
        select.appendChild(unknown);
        Array.from(manualCategorySelect?.options || []).forEach(option => {
            select.appendChild(new Option(option.text, option.value));
        });
        select.value = selectedId ? String(selectedId) : "";
        return select;
    }

    function unitOptions(unit) {
        const select = document.createElement("select");
        Array.from(manualUnitSelect?.options || []).forEach(option => {
            select.appendChild(new Option(option.text, option.value));
        });
        const value = unit && unit !== "-" ? unit : "";
        if (value && !Array.from(select.options).some(option => option.value === value)) {
            select.appendChild(new Option(value, value));
        }
        select.value = value;
        return select;
    }

    function addReceiptField(label, control) {
        const wrapper = document.createElement("label");
        wrapper.className = "receipt-item-field";
        wrapper.append(document.createTextNode(label), control);
        return wrapper;
    }

    function renderReceiptItems(items) {
        receiptItemsList.innerHTML = "";
        items.forEach((item, index) => {
            const card = document.createElement("section");
            card.className = "receipt-item-card";
            const title = document.createElement("h3");
            title.textContent = `食材 ${index + 1}`;
            const fields = document.createElement("div");
            fields.className = "receipt-item-fields";
            const name = document.createElement("input");
            name.type = "text"; name.value = item.name || ""; name.maxLength = 50; name.dataset.field = "name";
            const quantity = document.createElement("input");
            quantity.type = "number"; quantity.min = "1"; quantity.step = "1"; quantity.value = item.quantity > 0 ? item.quantity : 1; quantity.dataset.field = "quantity";
            const unit = unitOptions(item.unit); unit.dataset.field = "unit";
            const category = categoryOptions(item.categoryId); category.dataset.field = "categoryId";
            const deadline = document.createElement("input");
            deadline.type = "date"; deadline.value = validDate(item.expirationDate) ? item.expirationDate : ""; deadline.dataset.field = "deadline";
            fields.append(addReceiptField("食材名", name), addReceiptField("量", quantity), addReceiptField("単位", unit), addReceiptField("カテゴリー", category), addReceiptField("期限", deadline));
            card.append(title, fields);
            receiptItemsList.appendChild(card);
        });
    }

    function closeReceiptItems() {
        receiptItemsModal?.classList.remove("show");
        if (receiptReadMessage) receiptReadMessage.textContent = "";
    }

    readReceiptButton?.addEventListener("click", async () => {
        if (!receiptImageBlob) return;
        readReceiptButton.disabled = true;
        readReceiptButton.textContent = "読み取り中…";
        try {
            const formData = new FormData();
            formData.append("image", receiptImageBlob, "receipt.jpg");
            const response = await fetch("/api/receipts/analyze", { method: "POST", body: formData });
            const data = await response.json().catch(() => ({}));
            if (!response.ok) throw new Error(data.message || "レシートの読み取りに失敗しました。");
            closeReceiptPreviewModal();
            renderReceiptItems(data.items || []);
            receiptItemsModal?.classList.add("show");
        } catch (error) {
            alert(error.message || "レシートの読み取りに失敗しました。");
        } finally {
            readReceiptButton.disabled = false;
            readReceiptButton.textContent = "読み取り開始";
        }
    });

    closeReceiptItemsModal?.addEventListener("click", closeReceiptItems);
    cancelReceiptItems?.addEventListener("click", closeReceiptItems);

    bulkAddReceiptItems?.addEventListener("click", async () => {
        const cards = Array.from(receiptItemsList?.querySelectorAll(".receipt-item-card") || []);
        const items = cards.map(card => {
            const value = field => card.querySelector(`[data-field="${field}"]`)?.value || "";
            const unit = value("unit");
            return {
                name: value("name").trim(),
                categoryId: value("categoryId") ? Number(value("categoryId")) : null,
                amount: `${value("quantity") || "1"}${unit}`,
                deadline: value("deadline") || null
            };
        });
        const missingDeadline = items.find(item => item.deadline === null || item.deadline === undefined || item.deadline === "");
        if (missingDeadline) {
            receiptReadMessage.textContent = "期限を入力してください。";
            return;
        }
        const missingCategory = items.find(item => item.categoryId === null || item.categoryId === undefined || item.categoryId === "");
        if (missingCategory) {
            receiptReadMessage.textContent = "カテゴリーを入力してください。";
            return;
        }
        const invalid = cards.find((card, index) => !items[index].name || Number(card.querySelector('[data-field="quantity"]')?.value) < 1);
        if (invalid) {
            receiptReadMessage.textContent = "食材名と1以上の量を入力してください。";
            return;
        }
        if (!window.confirm(`以下の ${items.length} 件を追加します。よろしいですか？`)) return;
        bulkAddReceiptItems.disabled = true;
        try {
            const response = await fetch("/api/receipts/bulk-add", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ items })
            });
            const data = await response.json().catch(() => ({}));
            if (!response.ok) throw new Error(data.message || "食材の登録に失敗しました。");
            window.location.href = "/";
        } catch (error) {
            receiptReadMessage.textContent = error.message || "食材の登録に失敗しました。";
        } finally {
            bulkAddReceiptItems.disabled = false;
        }
    });

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
