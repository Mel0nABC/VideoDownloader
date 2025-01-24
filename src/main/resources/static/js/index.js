let updateData = false;
const YELLOW_STATUS = "rgb(255, 235, 156)";
const RED_STATUS = "rgb(255, 199, 206)";
const GREEN_STATUS = "rgb(198, 239, 206)";
var finish = false;;
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


        if (checkRow(urlValue)) {
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

    const texto = `<article id="${jsonData.webpage_url}" class="down-box-info">

        <div class="video-down-info">
            <h3 id="fulltitle" class="articleTittle">${jsonData.fulltitle}</h3>
            <img id="thumbnail" src="${jsonData.thumbnail}" class="articleImg">
        </div>
    
        <div class="video-down-options">
            <label for="selectQuality">Calidad de imagen:</label>
            <select id="selectQuality" class="selectQuality">
                <option value="selecciona">Selecciona una opción</option>
                <option value="opcion1">640x480</option>
                <option value="opcion2">1024x840</option>
                <option value="opcion3">1280x1024</option>
                <option value="opcion3">1440x1280</option>
            </select>
            <div id="wrapperBar${jsonData.webpage_url}" class="wrapper_2">
                <div id="progressBar${jsonData.webpage_url}" class="progress_2"></div>
            </div>
        </div>
        <div class="video-down-actions">
            <button data-btn-down-id="${mediaFile.id}" id="${jsonData.webpage_url}" name="btnDownload">Descargar</button>
            <button data-btn-del-id="${mediaFile.id}" id="${jsonData.webpage_url}" name="btnDelete">Eliminar</button>
        </div>
    </article>`


    const section = document.getElementById("section");
    section.innerHTML += texto;

    const btnDownloadList = document.getElementsByName("btnDownload");
    const btnDeleteList = document.getElementsByName("btnDelete");


    btnDownloadList.forEach(btnDown => {
        btnDown.addEventListener("click", e => {
            const btn = e.target;
            const url = btn.id;
            delBtnDelAddCancelBtn(btn);
            download(url);
        });
    });


    btnDeleteList.forEach(btn => {
        btn.addEventListener("click", e => {
            const url = e.target.id;
            delByUrl(url);
        });
    });

}

function delBtnDelAddCancelBtn(btnDown) {
    btnDown.disabled = true;
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
        });
    });
}

function delBtnCancelAddDelBtn(btnCancel) {
    const locationAdd = btnCancel.parentElement;
    const newDelBtn = document.createElement("button");
    newDelBtn.textContent = "Eliminar";
    newDelBtn.name = "btnDelete";
    newDelBtn.id = btnCancel.id;
    finish = true;
    locationAdd.appendChild(newDelBtn);
    btnCancel.remove();

    const btnDellList = document.getElementsByName("btnDelete");
    btnDellList.forEach(btnDel => {
        btnDel.addEventListener("click", e => {
            const url = e.target.id;
            delByUrl(url);
        });
    });

}


function download(url) {

    const formData = new FormData();
    formData.append("url", url);
    let options = {
        method: "POST",
        body: formData
    }

    fetch(`/download`, options)
        .then(res => res.json())
        .then(response => {
            updateTable();
        })
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
                checkStatusRow(mediaFile.url, mediaFile.downloaded, mediaFile.status, mediaFile.progressDownload);
            });
        })
}



async function updateTable() {
    updateData = true;
    let actualBtnDown;
    while (updateData) {
        let options = {
            method: "POST",
        }
        var status = null;
        var downloaded = null;
        var id = null;
        fetch(`/getInfo`, options)
            .then(res => res.json())
            .then(response => {
                response.forEach(element => {
                    const mediaFile = element.mediaFile;
                    id = mediaFile.id
                    url = mediaFile.url;
                    downloaded = mediaFile.downloaded;
                    status = element.status
                    updateBarProgress(url, downloaded, status)
                    if (downloaded === true) {
                        checkStatusRow(url, downloaded, "FINISH");
                        const cancelBtn = document.querySelector('[data-btn-cancel-id="' + id + '"]');
                        delBtnCancelAddDelBtn(cancelBtn);
                    } else {
                        checkStatusRow(url, downloaded, status);
                        actualBtnDown = document.querySelector('[data-btn-down-id="' + id + '"]')
                        if (!actualBtnDown.disabled && !finish)
                            delBtnDelAddCancelBtn(actualBtnDown);
                    }
                });

            })
        await new Promise(resolve => setTimeout(resolve, 300));
    }
};




function updateBarProgress(url, estado, status) {
    const progressBar = document.getElementById("progressBar" + url);
    if (progressBar === null)
        return;
    if (status === "Recoding") {
        progressBar.style.width = 100 + '%';
    } else {
        progressBar.style.width = status;
    }
}

function checkStatusRow(url, downloaded, status, progressDownload) {
    const progressBar = document.getElementById("progressBar" + url);
    if (progressBar === null)
        return;
    if (downloaded == true) {
        progressBar.style.width = 100 + '%';
    } else {

        if (progressBar != null)
            progressBar.style.width = progressDownload;

        if (status == "ERROR") {
            console.log(status)
        }
    }
}


function checkRow(url) {
    const articleCheck = document.getElementById(url);
    if (articleCheck != null) {
        articleCheck.classList.add("blink");

        articleCheck.addEventListener('animationiteration', (e) => {
            const animationCount = parseInt(e.elapsedTime / 1);
            if (animationCount >= 3) {
                articleCheck.classList.remove("blink");
            }
        });
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