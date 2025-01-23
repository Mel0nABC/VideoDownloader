let updateData = false;
const YELLOW_STATUS = "rgb(255, 235, 156)";
const RED_STATUS = "rgb(255, 199, 206)";
const GREEN_STATUS = "rgb(198, 239, 206)";

window.onload = function () {

    firstLoad();
    updateTable();
    // checkUpdate();


    const btnAddDownload = document.getElementById("btnAddDownload");
    var urlValue;
    btnAddDownload.addEventListener("click", function () {
        const urlElement = document.getElementById("url");
        urlValue = removeAfterAmpersand(urlElement.value);

        if (isblack(urlValue)) {
            alert("Debe introducir alguna dirección web.")
            return;
        }

        (async () => {
            try {

                loadingStart();


                const exit = await checkUrlExist(urlValue);
                if (exit) {
                    console.log("Ya existe");
                    return;
                }

                const jsonData = await getVideoMetada(urlValue);
                if (jsonData.respuesta == "error") {
                    console.log("HA OCURRIDO ALGUN ERROR.")
                    return;
                }

                addDownload(jsonData);

                const downloadExist = document.getElementById(jsonData.id);

                if (downloadExist === null)
                    console.log("ha habido un error");


                addUrlBBDD(urlValue, JSON.stringify(jsonData));

            } catch (error) {
                console.log(error);
            } finally {
                loadingStop();
            }


            urlElement.value = "";
        })();
    });

};


function isblack(url) {
    return !url.trim();
}

async function checkUrlExist(url) {


    const formData = new FormData();
    formData.append("url", url)

    let options = {
        method: "POST",
        body: formData
    }
    const res = await fetch(`/checkUrlExist`, options);
    return res.json();
}

function removeAfterAmpersand(url) {
    return url.split('&')[0];
}


function addDownload(jsonData) {

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
    
    
    
            <div class="wrapper_2">
                <div id="progressBar${jsonData.webpage_url}" class="progress_2"></div>
            </div>
    
    
    
        </div>
    
        <div class="video-down-actions">
            <button id="${jsonData.webpage_url}" name="btnDownload">Descargar</button>
            <button id="${jsonData.webpage_url}" name="btnDelete">Eliminar</button>
        </div>
    </article>`


    const section = document.getElementById("section");
    section.innerHTML += texto;

    const btnDownloadList = document.getElementsByName("btnDownload");
    const btnDeleteList = document.getElementsByName("btnDelete");


    btnDownloadList.forEach(btnDown => {
        btnDown.addEventListener("click", e => {
            console.log("BTN DOWNLOAD")
            const btn = e.target;
            const url = btn.id;
            delBtnDelAddCancelBtn(btn);
            download(url);
        });
    });


    btnDeleteList.forEach(btn => {
        btn.addEventListener("click", e => {
            const url = e.target.id;
            console.log(url)
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
    btnCancel.remove();
    const newDelBtn = document.createElement("button");
    newDelBtn.textContent = "Eliminar";
    newDelBtn.name = "btnDelete";
    newDelBtn.id = btnCancel.id;

    locationAdd.appendChild(newDelBtn);
    const btnDellList = document.getElementsByName("btnDelete");
    btnDellList.forEach(btnDel => {
        btnDel.addEventListener("click", e => {
            const url = e.target.id;
            delByUrl(url);
        });
    });

}


async function getVideoMetada(url) {
    const formData = new FormData();
    formData.append("url", url)
    const options = {
        method: "POST",
        body: formData
    };
    const resposne = await fetch("/getVideoMetada", options);
    const data = await resposne.json();

    return data;
}

async function addUrlBBDD(url, jsonData) {

    const formData = new FormData();
    formData.append("url", url);
    formData.append("jsonData", jsonData);
    let options = {
        method: "POST",
        body: formData
    }
    const resposne = await fetch("/addUrlBBDD", options);
    const data = await resposne.json();

    return data.mediaFile.downloaded;
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
                const jsonData = element.jsonData;
                const jsonDataBBDD = JSON.parse(jsonData);
                addDownload(jsonDataBBDD)
                const mediaFile = element;
                checkStatusRow(mediaFile.url, mediaFile.downloaded, mediaFile.status, mediaFile.progressDownload);
            });
        })
}


async function updateTable() {
    updateData = true;
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


                //Paramos el bucle si no hay articles.
                if (document.getElementsByTagName("article").length === 0)
                    updateData = false;

                response.forEach(element => {
                    const mediaFile = element.mediaFile;
                    url = mediaFile.url;
                    downloaded = mediaFile.downloaded;
                    status = element.status
                    updateBarProgress(url, downloaded, status)
                    if (downloaded == true) {
                        checkStatusRow(url, downloaded, "FINISH");
                        const downBtnList = document.getElementsByName("btnDownload")
                        downBtnList.forEach(btn => {
                            if (url === btn.id)
                                if (btn.disabled === true) {

                                    const cancelBtnList = document.getElementsByName("btnCancel")
                                    cancelBtnList.forEach(btn => {
                                        if (url === btn.id)
                                            delBtnCancelAddDelBtn(btn);
                                    })

                                }
                        })
                    } else {
                        checkStatusRow(url, downloaded, status);
                        const downBtnList = document.getElementsByName("btnDownload")
                        downBtnList.forEach(btn => {
                            if (url === btn.id)
                                if (btn.disabled === false) {
                                    delBtnDelAddCancelBtn(btn);
                                }
                        })
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
    if (status == "Recoding") {
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
            // console.log(status)
        }
    }
}


// //Comprobamos si existe el archivo en la lista de descargas
function checkRow(id) {

    const rowCheck = document.getElementById("row" + id);
    if (rowCheck != null) {

        // addBtnEliminar(id)

        rowCheck.classList.add("blink");

        // Eliminar la clase 'blink' después de 3 repeticiones (3 segundos)
        rowCheck.addEventListener('animationiteration', (e) => {
            const animationCount = parseInt(e.elapsedTime / 1); // Tiempo total de animación por ciclo
            if (animationCount >= 3) {
                rowCheck.classList.remove("blink"); // Elimina la clase cuando haya terminado
            }
        });
        return true;
    }
    return false;
}


function delByUrl(url) {
    console.log("DELE -> " + url)
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



