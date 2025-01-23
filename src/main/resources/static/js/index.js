let updateData = false;
const YELLOW_STATUS = "rgb(255, 235, 156)";
const RED_STATUS = "rgb(255, 199, 206)";
const GREEN_STATUS = "rgb(198, 239, 206)";
window.onload = function () {

    firstLoad();
    // checkUpdate();


    const btnAddDownload = document.getElementById("btnAddDownload");

    btnAddDownload.addEventListener("click", function () {

        if (isblack(url.value)) {
            alert("Debe introducir alguna dirección web.")
            return;
        }

        (async () => {

            if (await checkUrlExist(url.value)) {
                console.log("Ya existe");
                return;
            }
            console.log("palante")
            const jsonData = await getVideoMetada(url);
            if (jsonData.respuesta == "error") {
                console.log("HA OCURRIDO ALGUN ERROR.")
                return;
            }

            addDownload(jsonData);

            const downloadExist = document.getElementById(jsonData.id);

            if (downloadExist == null)
                console.log("ha habido un error");

            addUrlBBDD(url, JSON.stringify(jsonData));
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

    const resposne = await fetch("/addUrlBBDD", options);
    const data = await resposne.json();
    const res = await fetch(`/getUrl`, options);
    return await res.json();
}


function addDownload(jsonData) {
    // const array = datos.formats;
    // console.log(array)
    // array.forEach(dato => {
    //     console.log("DATOS -> " + dato[1])
    // });


    const texto = `<article id="${jsonData.id}" class="down-box-info">

        <div class="video-down-info">
            <h3 id="fulltitle" class="articleTittle">${jsonData.fulltitle}</h3>
            <img id="thumbnail" src="${jsonData.thumbnail}" class="articleImg">
        </div>
    
        <div class="video-down-options">
            <label for="selectQuality">Calidad de imagen:</label>
            <select id="selectQuality" class="video-down-quality">
                <option value="selecciona">Selecciona una opción</option>
                <option value="opcion1">640x480</option>
                <option value="opcion2">1024x840</option>
                <option value="opcion3">1280x1024</option>
                <option value="opcion3">1440x1280</option>
            </select>
    
    
    
            <div class="wrapper_2">
                <div class="progress_2"></div>
            </div>
    
    
    
        </div>
    
        <div class="video-down-actions">
            <button>Descargar</button>
            <button>Cancelar</button>
            <button>Eliminar</button>
        </div>
    </article>`

    const section = document.getElementById("section");
    section.innerHTML += texto;


}

async function getVideoMetada(url) {
    const formData = new FormData();
    formData.append("url", url.value)
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
    formData.append("url", url.value);
    formData.append("jsonData", jsonData);
    let options = {
        method: "POST",
        body: formData
    }
    const resposne = await fetch("/addUrlBBDD", options);
    const data = await resposne.json();

    return data.mediaFile.downloaded;
}


function download(idDownload) {
    // const url = document.getElementById("url");
    // const soloAudio = document.getElementById("soloAudio").checked;
    // const audioFormatMp3 = document.getElementById("audioFormatMp3").checked;
    // const formData = new FormData();
    // formData.append("soloAudio", soloAudio);
    // formData.append("audioFormatMp3", audioFormatMp3);
    // formData.append("url", url.value);
    // url.value = "";
    // formData.append("idDownload", idDownload);



    // let options = {
    //     method: "POST",
    //     body: formData
    // }

    // fetch(`/download`, options)
    //     .then(res => res.json())
    //     .then(response => {
    //         let mediaFile = response.mediaFile;

    //         if (checkRow(mediaFile.id) === true)
    //             return;

    //         addRow(mediaFile.id, mediaFile.url);
    //     })
    addRow("", "");
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
                // console.log(element)
                // console.log(element.jsonData)
                const jsonData = element.jsonData;
                const jsonDataBBDD = JSON.parse(jsonData);
                addDownload(jsonDataBBDD)
                // checkStatusRow(mediaFile.id, mediaFile.downloaded, mediaFile.status);
            });
            // updateTable();
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

                response.forEach(element => {
                    id = element.mediaFile.id;
                    downloaded = element.mediaFile.downloaded;
                    status = element.status
                    const mediaFile = element.mediaFile;
                    updateBarProgress(id, downloaded, status)
                    if (downloaded == true) {
                        checkStatusRow(id, downloaded, "FINISH");
                    } else {
                        checkStatusRow(id, downloaded, status);
                    }
                });

            })
        await new Promise(resolve => setTimeout(resolve, 300));
    }
};

function updateBarProgress(id, estado, status) {
    console.log("ID -> " + id + " - " + estado + " - " + status)
    const progressBar = document.getElementById("progressBar" + id);
    const porcenShell = document.getElementById("porcenShell" + id);
    const downloadRow = document.getElementById("row" + id);

    if (status == "Recoding") {
        progressBar.style.width = 100 + '%';
    } else {
        progressBar.style.width = status;
    }
    porcenShell.innerHTML = status;

}

function checkStatusRow(id, downloaded, status) {
    const downloadRow = document.getElementById("row" + id);
    const progressBar = document.getElementById("progressBar" + id);
    const porcenShell = document.getElementById("porcenShell" + id);

    if (downloadRow == null)
        return;

    if (downloaded == true) {
        downloadRow.style.backgroundColor = GREEN_STATUS;
        progressBar.style.width = 100 + '%';
        addBtnEliminar(id);
    } else {
        if (status == "ERROR") {
            downloadRow.style.backgroundColor = RED_STATUS;
            addBtnEliminar(id);
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



function addBtnCancel(id) {
    const checkBtnExist = document.getElementById("deleteBtn" + id);

    if (checkBtnExist != null) {
        return;
    }
    const cell4 = document.getElementById("actionShell" + id);
    const actionBtnCancel = document.createElement("button");
    actionBtnCancel.id = "cancel" + id;
    actionBtnCancel.textContent = "Cancelar"
    cell4.appendChild(actionBtnCancel);

    actionBtnCancel.addEventListener("click", e => {
        const formData = new FormData();
        const url = document.getElementById("url" + id);
        formData.append("url", url.textContent);

        let options = {
            method: "POST",
            body: formData
        }

        fetch("/stopThread", options)
            .then(res => res.json())
            .then(response => {
                if (response == true) {
                    delRow(id, response)
                } else {
                    alert("Ha ocurrido algún problema inesperado para cancelar la descarga.")
                }
            })
    });

}

function checkCancelBtnAndDel(id) {
    const cancelBtn = document.getElementById("cancel" + id);
    if (cancelBtn == null)
        return;
    cancelBtn.remove();
}

function addBtnEliminar(id) {
    const checkBtnExist = document.getElementById("deleteBtn" + id);
    if (checkBtnExist != null)
        return;

    checkCancelBtnAndDel(id);
    const cell4 = document.getElementById("actionShell" + id);
    const actionBtn = document.createElement("button");
    actionBtn.id = "deleteBtn" + id;
    actionBtn.textContent = "Eliminar"
    cell4.appendChild(actionBtn);

    actionBtn.addEventListener("click", e => {
        delByUrl(id);
    });
}

function delByUrl(id) {
    const formData = new FormData();
    const url = document.getElementById("url" + id);
    formData.append("url", url.textContent);

    let options = {
        method: "POST",
        body: formData
    }

    fetch(`/delByUrl`, options)
        .then(res => res.json())
        .then(response => {
            delRow(id, response);
        })
}

function delRow(id, response) {
    const rowToDel = document.getElementById("row" + id);
    if (response == true) {
        rowToDel.remove();
    } else {
        rowToDel.style.backgroundColor = RED_STATUS;
    }
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

function getVideoFromats(url) {

    console.log("TEST")
    const infoTable = document.getElementById("infoTable");
    // infoTable.visibility = visible;

    if (infoTable != null)
        infoTable.remove();


    const formData = new FormData();
    formData.append("url", url.value)
    var btnSelectedId = "";
    let options = {
        method: "POST",
        body: formData
    }

    fetch('/getVideoFormats', options)
        .then(res => res.json())
        .then(response => {

            const table = document.createElement("table");
            const thead = document.createElement("thead");

            const headerRow = document.createElement("tr");
            const headerRow2 = document.createElement("tr");
            const th = document.createElement("th");
            const th2 = document.createElement("th");

            table.id = "infoTable";
            th.textContent = "OPCIONES DE DESCARGA, ELIJA UNA";
            th2.textContent = "ID EXT RESOLUTION FPS CH | FILESIZE TBR PROTO | VCODEC VBR ACODEC ABR ASR MORE INFO";
            const btnClose = document.createElement("button");
            btnClose.id = "btnCloseDownloadOptions";
            btnClose.className = "close-window-button";
            btnClose.textContent = "X";


            headerRow.appendChild(th);
            th.appendChild(btnClose);
            headerRow2.appendChild(th2);
            thead.appendChild(headerRow);
            thead.appendChild(headerRow2);
            table.appendChild(thead);

            // Crear el cuerpo de la tabla
            const tbody = document.createElement("tbody");

            // Agregar las líneas al cuerpo de la tabla
            response.forEach((line, index) => {
                const row = document.createElement("tr");
                const id = line.substring(0, 7);
                row.id = id;
                row.className = "btnFormat";

                // Columna de número de línea
                const lineNumberCell = document.createElement("td");
                lineNumberCell.textContent = line;

                row.appendChild(lineNumberCell);
                tbody.appendChild(row);
            });

            table.appendChild(tbody);

            const popup = document.createElement("div");
            popup.className = "pop-up-window";

            const contentpopup = document.createElement("div");
            contentpopup.className = "content-pop-up-window";

            const titleContainer = document.createElement("titleContainer");
            titleContainer.id = "titleContainer";
            titleContainer.appendChild(table);

            contentpopup.appendChild(titleContainer);
            popup.appendChild(contentpopup);

            document.getElementsByTagName("body")[0].appendChild(popup);



            const rowBotons = document.getElementsByClassName("btnFormat");
            for (const btn of rowBotons) {
                btn.addEventListener("click", e => {
                    if (btnSelectedId != "")
                        document.getElementById(btnSelectedId).style.backgroundColor = "white";

                    btn.style.backgroundColor = "#cce5ff";
                    btnSelectedId = btn.id;
                })

                btn.addEventListener("dblclick", e => {
                    download(btn.id);
                    table.remove();
                    btnSelectedId = "";
                })
            };

            btnClose.addEventListener("click", e => {
                popup.remove();
                btnSelectedId = "";
            })
        })
}


