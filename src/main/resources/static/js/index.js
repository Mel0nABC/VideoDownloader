let updateData = false;
const YELLOW_STATUS = "rgb(255, 235, 156)";
const RED_STATUS = "rgb(255, 199, 206)";
const GREEN_STATUS = "rgb(198, 239, 206)";

window.onload = function () {

    firstLoad();
    updateTable();
    checkUpdate();

    const btnAddDownload = document.getElementById("btnAddDownload");
    var urlValue;
    btnAddDownload.addEventListener("click", function () {
        const urlElement = document.getElementById("url");
        urlValue = removeAfterAmpersand(urlElement.value);

        if (isblack(urlValue)) {
            alert("Debe introducir alguna dirección web.")
            return;
        }


        if (checkRowBlink(urlValue)) {
            return;
        }


        (async () => {
            try {
                loadingStart();

                const formData = new FormData();
                formData.append("url", urlValue);

                const options = {
                    method: "POST",
                    body: formData
                }

                const consulta = await fetch("/addDownload", options);
                const response = await consulta.text();

                if (response === "error") {
                    return;
                }

                const mediaFile = JSON.parse(response);
                addDownload(mediaFile);
                updateBarProgress(mediaFile)
            } catch (error) {
                console.log(error);
            } finally {
                loadingStop();
                urlElement.value = "";

            }
        })();
    });



}
function isblack(url) {
    return !url.trim();
}


function removeAfterAmpersand(url) {
    return url.split('&')[0];
}


function addDownload(mediaFile) {
    const jsonData = JSON.parse(mediaFile.jsonData);

    const texto = `<section id="section">
            <article id="${jsonData.webpage_url}">
                <h2 id="fulltitle" class="articleTittle">${jsonData.fulltitle}</h2>
                <div class="down-box-info">
                    <div class="video-down-info">
                        <img id="thumbnail" src="${jsonData.thumbnail}" class="articleImg">
                    </div>
                    <div class="video-down-options">
                        <div>
                            <label for="selectQuality">Calidad de imagen:</label>
                            <select id="selectQuality" class="selectQuality">
                                <option value="selecciona">Selecciona una opción</option>
                                <option value="opcion1">640x480</option>
                                <option value="opcion2">1024x840</option>
                                <option value="opcion3">1280x1024</option>
                                <option value="opcion3">1440x1280</option>
                            </select>
                        </div>
                        <div>
                            <label for="soloAudio">Descargar sólo audio:</label>
                            <input type="checkbox" id="soloAudio">
                        </div>
                        <div>
                            <label for="soloAudio">Audio en MP3:</label>
                            <input type="checkbox" id="mp3">
                        </div>
                    </div>
                    <div class="video-down-actions">
                        <button data-btn-down-id="${mediaFile.id}" id="${jsonData.webpage_url}"
                            name="btnDownload">Descargar</button>
                        <button data-btn-del-id="${mediaFile.id}" id="${jsonData.webpage_url}"
                            name="btnDelete">Eliminar</button>
                    </div>
                </div>
                <div id="wrapperBar${jsonData.webpage_url}" class="wrapper_2">
                    <label id="progressLabel${mediaFile.id}">Descarga no iniciada</label>
                    <div id="progressBar${jsonData.webpage_url}" class="progress_2"></div>
                </div>
            </article>`


    const section = document.getElementById("section");
    section.innerHTML = texto + section.getHTML();

    const btnDownloadList = document.getElementsByName("btnDownload");
    const btnDeleteList = document.getElementsByName("btnDelete");


    btnDownloadList.forEach(btnDown => {
        btnDown.addEventListener("click", async e => {
            const btn = e.target;
            const url = btn.id;
            const data = await download(url);
            const texto = data.mediaFile;
            try {
                if (texto.includes("Error")) {
                    checkButtonsStatus();
                    alert(texto);
                }
            } catch (error) {
                updateTable();
                delBtnDelAddCancelBtn(btn);
            } finally {
                updateBarProgress(mediaFile);
            }
        });
    });


    btnDeleteList.forEach(btn => {
        btn.addEventListener("click", e => {
            const url = e.target.id;
            delByUrl(url);
        });
    });
    document.getElementById("url").value = "";
}

async function download(url) {

    const formData = new FormData();
    formData.append("url", url);
    let options = {
        method: "POST",
        body: formData
    }
    const data = await fetch(`/download`, options);
    return await data.json();
}

function delBtnDelAddCancelBtn(btnDown) {
    btnDown.disabled = true;
    disableButtonColors(btnDown);
    document.getElementsByName("btnDelete").forEach(btnDel => {

        if (btnDown.id === btnDel.id)
            btnDel.remove();
    })

    const newCancelBtn = document.createElement("button");
    newCancelBtn.textContent = "Cancelar";
    newCancelBtn.name = "btnCancel";
    newCancelBtn.id = btnDown.id;
    newCancelBtn.setAttribute("data-btn-cancel-id", btnDown.getAttribute("data-btn-down-id"));
    const locationAdd = btnDown.parentElement;
    locationAdd.appendChild(newCancelBtn);
    const btnCancelList = document.getElementsByName("btnCancel");
    btnCancelList.forEach(btnCancel => {
        btnCancel.addEventListener("click", e => {
            const url = e.target.id;
            cancelDownload(url);
            delBtnCancelAddDelBtn(btnCancel);
            btnDown.disabled = false;
            enableButtonColors(btnDown);
        });
    });
}

function disableButtonColors(btn) {
    btn.style.setProperty('background', 'linear-gradient(to right, rgba(128, 128, 128, 0.1) 1%, transparent 40%, transparent 60%, rgba(128, 128, 128, 0.1) 100%)');
    btn.style.setProperty('box-shadow', 'inset 0 0 10px rgba(128, 128, 128, 0.4), 0 0 9px 3px rgba(128, 128, 128, 0.1)');
    btn.style.setProperty('border-color', 'gray');
    btn.style.setProperty('color', 'gray');
    btn.classList.add('no-hover')
}

function enableButtonColors(btn) {
    btn.style.setProperty('--green', '#1BFD9C');
    btn.style.setProperty('background', 'linear-gradient(to right, rgba(27, 253, 156, 0.1) 1%, transparent 40%, transparent 60%, rgba(27, 253, 156, 0.1) 100%)');
    btn.style.setProperty('box-shadow', 'inset 0 0 10px rgba(27, 253, 156, 0.4), 0 0 9px 3px rgba(27, 253, 156, 0.1)');
    btn.style.setProperty('border-color', 'var(--green)');
    btn.style.setProperty('color', 'var(--green)');
    btn.classList.remove('no-hover')
}

function delBtnCancelAddDelBtn(btnCancel) {
    const locationAdd = btnCancel.parentElement;
    const newDelBtn = document.createElement("button");
    const btnCancelIdNumber = btnCancel.getAttribute("data-btn-cancel-id")
    const btnDown = document.querySelector('[data-btn-down-id="' + btnCancelIdNumber + '"]')
    console.log('[data-btn-down-id="' + btnCancelIdNumber + '"]')
    enableButtonColors(btnDown);
    newDelBtn.textContent = "Eliminar";
    newDelBtn.name = "btnDelete";
    newDelBtn.id = btnCancel.id;
    newDelBtn.setAttribute("data-btn-del-id", btnCancel.id)
    locationAdd.appendChild(newDelBtn);
    btnCancel.remove();
    finish = true;
    const btnDellList = document.getElementsByName("btnDelete");
    btnDellList.forEach(btnDel => {
        btnDel.addEventListener("click", e => {
            const url = e.target.id;
            delByUrl(url);
        });
    });
}

function cancelDownload(url) {
    const formData = new FormData();
    formData.append("url", url);

    let options = {
        method: "POST",
        body: formData
    }

    fetch("/stopThread", options)
        .then(res => res.json())
        .then(response => {
            if (response == false) {
                alert("Ha ocurrido algún problema inesperado para cancelar la descarga.")
            }
        })
}

function firstLoad() {

    const url = document.getElementById("url").value;
    const formData = new FormData();
    formData.append("url", url)

    let options = {
        method: "POST",
        body: formData
    }

    fetch(`/getAllURL`, options)
        .then(res => res.json())
        .then(response => {
            response.forEach(element => {
                const mediaFile = element;
                addDownload(mediaFile);
                updateBarProgress(mediaFile);
                checkButtonsStatus();
            });
        })
}



async function updateTable() {
    updateData = true;
    while (updateData) {
        updateData = false;
        var status = null;
        var downloaded = null;
        var id = null;
        const response = await getDownloadingThreads();
        response.forEach(element => {
            updateData = true;
            updateBarProgress(element.mediaFile)
            checkButtonsStatus();
        });
        checkButtonsStatus();
        await new Promise(resolve => setTimeout(resolve, 300));
    }
};


function updateBarProgress(mediaFile) {

    const url = mediaFile.url;
    const progressDownload = mediaFile.progressDownload;
    const id = mediaFile.id;
    const progressBar = document.getElementById("progressBar" + url);
    const progressLabel = document.getElementById("progressLabel" + id)


    if (progressBar === null)
        return;
    if (progressDownload === "100%") {
        progressBar.style.width = 100 + '%';
        progressLabel.innerHTML = "Descargado"
    } else {
        progressBar.style.width = progressDownload;
        progressLabel.innerHTML = progressDownload;
    }
}


async function getDownloadingThreads() {
    let options = {
        method: "POST",
    }
    const data = await fetch(`/getInfo`, options);
    return data.json();
}

async function checkButtonsStatus() {
    const listaDescargas = await getDownloadingThreads();
    const btnDownloadList = document.getElementsByName("btnDownload");

    btnDownloadList.forEach(btn => {
        const id = btn.getAttribute("data-btn-down-id");
        const btnCancel = document.querySelector('[data-btn-cancel-id="' + id + '"]')

        console.log(listaDescargas.length)
        console.log(btnCancel)
        if (listaDescargas.length === 0)
            if (btnCancel != null) {
                delBtnCancelAddDelBtn(btnCancel);
                console.log("BTN LALALALAL")
            }


        listaDescargas.forEach(descarga => {
            if (btn.id === descarga.mediaFile.url) {
                if (!btn.disabled) {
                    delBtnDelAddCancelBtn(btn);
                }
            } else {

            }
        });
    });
}



function checkRowBlink(url) {
    const articleCheck = document.getElementById(url);
    if (articleCheck != null) {
        articleCheck.classList.add("blink");
        articleCheck.scrollIntoView({ behavior: 'smooth', block: 'center' });
        articleCheck.addEventListener('animationiteration', (e) => {
            const animationCount = parseInt(e.elapsedTime / 1);
            if (animationCount >= 3) {
                articleCheck.classList.remove("blink");
            }
        });
        document.getElementById("url").value = "";
        return true;
    }
    return false;
}


function delByUrl(url) {
    const formData = new FormData();
    formData.append("url", url);

    let options = {
        method: "POST",
        body: formData
    }

    fetch(`/delByUrl`, options)
        .then(res => res.json())
        .then(response => {
            if (response == true) {
                delArticle(url);
                return true;
            }

            return false;

        })
}

function delArticle(url) {
    const articleToDel = document.getElementById(url);
    articleToDel.remove();
}


function checkUpdate() {

    let actualVersion;
    let latestVersion;
    let upToDate;
    let updated;
    let error;

    var actualVersionText = document.getElementById("actualVersion")
    var lastVersionText = document.getElementById("lastVersion")
    var updatedVersionText = document.getElementById("updatedVersion")

    const now = new Date(Date.now());

    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');

    const formattedDate = `${year}.${month}.${day}`;

    fetch('/checkYtUpdate', { method: 'POST' })
        .then(res => res.json())
        .then(response => {
            actualVersion = response.actualVersion;
            latestVersion = response.latestVersion;
            upToDate = response.upToDate;
            updated = response.updated;
            error = response.error;
            if (error == true) {
                var errorText = "Error";
                actualVersionText.textContent = "  " + errorText
                lastVersionText.textContent = "  " + errorText;
                updatedVersionText.textContent = "  " + errorText;
                return;
            }


            if (updated == true) {
                actualVersionText.textContent = "  " + latestVersion;
            } else {
                actualVersionText.textContent = "  " + actualVersion;
            }

            lastVersionText.textContent = "  " + latestVersion;
            updatedVersionText.textContent = "  " + formattedDate;
        })
}


