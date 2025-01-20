let updateData = false;
const YELLOW_STATUS = "rgb(255, 235, 156)";
const RED_STATUS = "rgb(255, 199, 206)";
const GREEN_STATUS = "rgb(198, 239, 206)";
window.onload = function () {

    firstLoad();
    checkUpdate();

    const btnDownMaxQuality = document.getElementById("btnDownloadMaxQuality");
    const btnDownloadOptions = document.getElementById("btnDownloadOptions");

    btnDownMaxQuality.addEventListener("click", function () {
        const url = document.getElementById("url");
        if (isblack(url.value)) {
            alert("Debe introducir alguna dirección web.")
            return;
        }
        download("null");
    });

    btnDownloadOptions.addEventListener("click", function () {
        const url = document.getElementById("url");
        if (isblack(url.value)) {
            alert("Debe introducir alguna dirección web.")
            return;
        }
        console.log(url)
        getVideoFromats(url);
    });
};

function isblack(url) {
    return !url.trim();
}


function download(idDownload) {
    const url = document.getElementById("url");
    const soloAudio = document.getElementById("soloAudio").checked;
    const audioFormatMp3 = document.getElementById("audioFormatMp3").checked;
    const formData = new FormData();
    formData.append("soloAudio", soloAudio);
    formData.append("audioFormatMp3", audioFormatMp3);
    formData.append("url", url.value);
    url.value = "";
    formData.append("idDownload", idDownload);



    let options = {
        method: "POST",
        body: formData
    }

    fetch(`/download`, options)
        .then(res => res.json())
        .then(response => {
            let mediaFile = response.mediaFile;

            if (checkRow(mediaFile.id) === true)
                return;

            addRow(mediaFile.id, mediaFile.url);
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

    fetch(`/firstLoad`, options)
        .then(res => res.json())
        .then(response => {
            response.forEach(element => {
                const mediaFile = element;
                addRow(mediaFile.id, mediaFile.url, 0);
                checkStatusRow(mediaFile.id, mediaFile.downloaded, mediaFile.status);
            });
            updateTable();
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

// Función para añadir una fila a la tabla
function addRow(id, url, status) {

    // Obtener la tabla
    const table = document.getElementById('mediaTable').getElementsByTagName('tbody')[0];

    // Crear una nueva fila
    const newRow = table.insertRow();
    newRow.id = "row" + id;

    // Crear celdas en la nueva fila
    const cell1 = newRow.insertCell(0); // Columna ID
    const cell2 = newRow.insertCell(1); // Columna URL
    const cell3 = newRow.insertCell(2); // Columna %
    const cell4 = newRow.insertCell(3); // Columna Acciones

    // Asignar los valores a las celdas
    cell1.textContent = id; // ID auto incremental
    cell2.innerHTML = `
                <div class="progress-container">
                    <div class="progress-bar" id="progressBar${id}"></div>
                </div>
                <div id="url${id}" class="url-text">${url}</div>
            `;
    cell3.id = "porcenShell" + id;
    cell3.textContent = 'WAIT'; // status aleatorio
    cell4.id = "actionShell" + id;
    addBtnCancel(id);
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


    const infoTable = document.getElementById("infoTable");

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
            btnClose.id = "btnCloseDownloadOptions"
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

            const titleContainer = document.getElementById("titleContainer");
            titleContainer.appendChild(table);

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
                table.remove();
                btnSelectedId = "";
            })
        })
}