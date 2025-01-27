let updateData = false;
const YELLOW_STATUS = "rgb(255, 235, 156)";
const RED_STATUS = "rgb(255, 199, 206)";
const GREEN_STATUS = "rgb(198, 239, 206)";

window.onload = function () {

    firstLoad();
    checkUpdateYtDlp()
    const btnAddDownload = document.getElementById("btnAddDownload");
    var urlValue;
    btnAddDownload.addEventListener("click", function () {
        const urlElement = document.getElementById("url");
        // urlValue = removeAfterAmpersand(urlElement.value);
        urlValue = urlElement.value;


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

    const intervalID = setInterval(() => {
        checkUpdatesDB();
    }, 5000);
}

async function checkUpdatesDB() {

    const urlDbList = await getAllUrl();
    const articleList = document.querySelectorAll("article");
    const sectionString = document.getElementById("section").innerHTML;

    if (urlDbList.length > 0 && updateData === false) {
        updateData = true;
        updateTable();
    } else if (urlDbList.length <= 0 && updateData === true) {
        updateData = false;
    }

    if (urlDbList.length < articleList.length) {
        articleList.forEach(article => {
            if (!JSON.stringify(urlDbList).includes(article.id)) {
                article.remove();
            }

        })
    }

    if (urlDbList.length > articleList.length) {
        urlDbList.forEach(urlDb => {
            if (!sectionString.includes(urlDb.url)) {
                addDownload(urlDb)
            }
        });
    }
}

async function updateTable() {
    while (updateData) {
        const articleList = document.querySelectorAll("section");
        var status = null;
        var downloaded = null;
        var id = null;
        const response = await getDownloadingThreads();
        response.forEach(element => {
            updateBarProgress(element.mediaFile)
            checkButtonsStatus();
        });
        checkButtonsStatus();
        await new Promise(resolve => setTimeout(resolve, 5000));
    }
    if (updateData)
        firstLoad();
};

function isblack(url) {
    return !url.trim();
}


function removeAfterAmpersand(url) {
    return url.split('&')[0];
}

function createTableFormats(url, id) {


    const formData = new FormData();
    formData.append("url", url);
    formData.append("id", id)

    const options = {
        method: "POST",
        body: formData
    }

    fetch("/getUrl", options)
        .then(res => res.json())
        .then(response => {

            const jsonData = JSON.parse(response.jsonData);

            var tabla = `<div id="containerFormatos" class="table-container">
                    <div class="table-data-container">
                        <h2>SELECCIÓN DE FORMATOS</h2>
                        <table class="containerTable">
                            <thead>
                                <tr>
                                    <th><h1>ID</h1></th>
                                    <th><h1>EXT</h1></th>
                                    <th><h1>RESOLUTION</h1></th>
                                    <th><h1>FILESIZE</h1></th>
                                    <th><h1>TBR</h1></th>
                                    <th><h1>VCODEC</h1></th>
                                    <th><h1>VBR</h1></th>
                                    <th><h1>ACODEC</h1></th>
                                    <th><h1>ABR</h1></th>
                                    <th><h1>MORE</h1></th>
                                   </tr>
                            </thead>
                            <tbody>`

            jsonData.formats.forEach(formats => {
                var tbrcalc = Number(formats.tbr).toFixed(0);
                var mbytes = formats.filesize / 1024 / 1024;
                var mbytesUnit = "MB"
                if (mbytes > 1024) {
                    mbytes = mbytes / 1024;
                    mbytesUnit = "GB"
                }
                mbytes = mbytes.toFixed(2);
                if (formats.filesize === undefined) {
                    mbytesUnit = "";
                    mbytes = "ND";
                }

                var vbrCalc = formats.vbr;

                if (vbrCalc === null) {
                    vbrCalc = "ND";
                } else {
                    vbrCalc = vbrCalc.toFixed(0);
                }



                tabla += `<tr id="${formats.format_id}" class="rowFormat">
                                    <td>${formats.format_id}</td>
                                    <td>${formats.ext}</td>
                                    <td>${formats.resolution}</td>
                                    <td>${mbytes}${mbytesUnit}</td>
                                    <td>${tbrcalc}K</td>
                                    <td>${formats.vcodec}</td>
                                    <td>${vbrCalc}K</td>
                                    <td>${formats.acodec}</td>
                                    <td>${formats.abr}</td>
                                    <td>${formats.format_note}</td>
                                </tr>`;
            })

            tabla += `</tbody>
                        </table>
                    </div>
                </div>`;

            const title = document.getElementsByClassName("title")[0];
            title.innerHTML += tabla;
            const cierre = document.createElement("button");
            cierre.textContent = "X";
            cierre.classList.add("cierre");
            cierre.id = "clsFormatSelection";
            document.getElementsByClassName("table-data-container")[0].appendChild(cierre)


            Array.from(document.getElementsByClassName("rowFormat")).forEach(row => {
                row.addEventListener("click", async td => {
                    const rowClicked = td.target.parentElement
                    const data = await download(url, rowClicked.id);

                    const texto = data.mediaFile;
                    try {
                        if (texto.includes("Error")) {
                            checkButtonsStatus();
                            alert(texto);
                        }
                    } catch (error) {
                    } finally {
                        updateBarProgress(data.mediaFile);
                        closeContainerFormats();
                    }
                })

            });

            cierre.addEventListener("click", e => {
                closeContainerFormats();

            });
        });
}

function closeContainerFormats() {
    document.getElementById("containerFormatos").remove();
}

function addDownload(mediaFile) {
    const jsonData = JSON.parse(mediaFile.jsonData);

    let titulo;
    let img;
    if (mediaFile.jsonData.includes("playlist_channel")) {
        titulo = jsonData.playlist;
        // img = jsonData.thumbnails
        // img = img[img.length - 1].url
        img = "/images/image.png"
    } else {
        titulo = jsonData.fulltitle;
        img = jsonData.thumbnail;
    }

    const texto = `<article id="${mediaFile.url}">
                <h2 id="fulltitle" class="articleTittle">${titulo}</h2>
                <div class="down-box-info">
                    <div class="video-down-info">
                        <img id="thumbnail" src="${img}" class="articleImg">
                    </div>

                    <div class="video-down-actions">
                        <button data-btn-down-id="${mediaFile.id}" id="${mediaFile.url}"
                            name="btnDownload">SELECCIÓN CALIDAD</button>
                        <button data-btn-DirectList-id="${mediaFile.id}" id="${mediaFile.url}"
                            name="btnListDirect">DESCARGA DIRECTA Y LISTAS</button> 
                        <button data-btn-del-id="${mediaFile.id}" id="${mediaFile.url}"
                            name="btnDelete">ELIMINAR</button>
                    </div>
                </div>
                <div id="wrapperBar${mediaFile.url}" class="wrapper_2">
                    <label id="progressLabel${mediaFile.id}">Descarga no iniciada</label>
                    <div id="progressBar${mediaFile.url}" class="progress_2"></div>
                </div>
                </article>`


    const section = document.getElementById("section");
    section.innerHTML = texto + section.innerHTML;
    const btnDownloadList = document.getElementsByName("btnDownload");
    const btnDirectList = document.getElementsByName("btnListDirect");

    const btnDeleteList = document.getElementsByName("btnDelete");


    btnDownloadList.forEach(btnDown => {
        btnDown.addEventListener("click", async e => {
            const url = e.target.id;
            const id = e.target.getAttribute("data-btn-down-id")
            createTableFormats(url, id);
        });
    });

    btnDirectList.forEach(btnDirect => {
        btnDirect.addEventListener("click", async e => {
            const url = e.target.id;

            const data = await download(url, "direct");

            const texto = data.mediaFile;
            try {
                if (texto.includes("Error")) {
                    checkButtonsStatus();
                    alert(texto);
                }
            } catch (error) {
            } finally {
                updateBarProgress(data.mediaFile);
            }
        })
    });



    btnDeleteList.forEach(btn => {
        btn.addEventListener("click", e => {
            const url = e.target.id;
            delByUrl(url);
        });
    });
    document.getElementById("url").value = "";
}

async function download(url, formatId) {

    const formData = new FormData();
    formData.append("url", url);
    formData.append("formatId", formatId);

    let options = {
        method: "POST",
        body: formData
    }
    const data = await fetch(`/download`, options);
    return await data.json();
}

function delBtnDelAddCancelBtn(btnDown) {
    const btnDirect = document.querySelector('[data-btn-directlist-id="' + btnDown.getAttribute("data-btn-down-id") + '"]')
    btnDown.disabled = true;
    btnDirect.disabled = true;
    disableButtonColors(btnDown);
    disableButtonColors(btnDirect);

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
            btnDirect.disabled = false;
            enableButtonColors(btnDown);
            enableButtonColors(btnDirect);
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
    btn.disabled = false;
}

function delBtnCancelAddDelBtn(btnCancel) {
    const locationAdd = btnCancel.parentElement;
    const newDelBtn = document.createElement("button");
    const btnCancelIdNumber = btnCancel.getAttribute("data-btn-cancel-id")
    const btnDown = document.querySelector('[data-btn-down-id="' + btnCancelIdNumber + '"]')
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

async function firstLoad() {
    const response = await getAllUrl();
    response.forEach(element => {
        const mediaFile = element;
        addDownload(mediaFile);
        updateBarProgress(mediaFile);
        checkButtonsStatus();
    });
}

async function getAllUrl() {
    const elements = await fetch(`/getAllURL`, { method: "POST" });
    return await elements.json();
}


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
        if (listaDescargas.length === 0)
            if (btnCancel != null) {
                delBtnCancelAddDelBtn(btnCancel);
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


function checkUpdateYtDlp() {

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


