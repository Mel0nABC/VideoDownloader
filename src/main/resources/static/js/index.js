let updateData = false;
const YELLOW_STATUS = "rgb(255, 235, 156)";
const RED_STATUS = "rgb(255, 199, 206)";
const GREEN_STATUS = "rgb(198, 239, 206)";
window.onload = function () {


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
                addRow(mediaFile.id, mediaFile.url, mediaFile.downloaded, 0);
            });
            updateData = true;
            updateTable();
        })





    const btnDown = document.getElementById("btnDownload");
    let id = null;
    let downloaded = null;
    let status = null;
    btnDown.addEventListener("click", function () {


        const url = document.getElementById("url").value;
        const soloAudio = document.getElementById("soloAudio").checked;
        const audioFormatMp3 = document.getElementById("audioFormatMp3").checked;
        const formData = new FormData();
        formData.append("soloAudio", soloAudio);
        formData.append("audioFormatMp3", audioFormatMp3);
        formData.append("url", url);


        let options = {
            method: "POST",
            body: formData
        }


        fetch(`/download`, options)
            .then(res => res.json())
            .then(response => {

                let mediaFile = response.mediaFile;

                if (response.respuesta == false) {
                    addRow(mediaFile.id, mediaFile.url, mediaFile.downloaded, mediaFile.exitCode);
                    return;
                }
                addRow(mediaFile.id, mediaFile.url, mediaFile.downloaded, 0);
                updateData = true;
                updateTable();
            })
        checkStatusRow(id, downloaded, status);
    });


    const updateTable = async function updateTable() {

        while (updateData) {
            let options = {
                method: "POST",
            }


            fetch(`/getInfo`, options)
                .then(res => res.json())
                .then(response => {
                    if (response == "") {
                        updateData = false;
                    }

                    response.forEach(element => {
                        console.log(element)
                        id = element.mediaFile.id;
                        downloaded = element.mediaFile.downloaded;
                        status = element.status
                        const mediaFile = element.mediaFile;
                        updateBarProgress(id, downloaded, status)
                    });

                })
            await new Promise(resolve => setTimeout(resolve, 300));
        }
    };

    updateTable();
};

//Comprobamos si existe el archivo en la lista de descargas
function checkRow(id, url, downloaded, status) {
    const rowCheck = document.getElementById("row" + id);
    if (rowCheck != null) {

        addBtnEliminar(id)

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

function addBtnEliminar(id) {

    const checkBtnExist = document.getElementById("deleteBtn" + id);

    if (checkBtnExist != null) {
        return;
    }
    const cell4 = document.getElementById("actionShell" + id);
    const actionBtn = document.createElement("button");
    actionBtn.id = "deleteBtn" + id;
    actionBtn.textContent = "Eliminar"
    cell4.appendChild(actionBtn);

    actionBtn.addEventListener("click", e => {
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

                const rowToDel = document.getElementById("row" + id);

                if (response == true) {
                    rowToDel.remove();
                } else {
                    rowToDel.style.backgroundColor = RED_STATUS;
                }


            })
    });
}

// Función para añadir una fila a la tabla
function addRow(id, url, downloaded, status) {


    if (checkRow(id, url, downloaded, status)) {
        return;
    }


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
    cell3.textContent = `${status}`; // status aleatorio
    cell4.id = "actionShell" + id;
    addBtnEliminar(id);
    checkStatusRow(id, downloaded, status);
}



function updateBarProgress(id, estado, status) {
    const progressBar = document.getElementById("progressBar" + id);
    const porcenShell = document.getElementById("porcenShell" + id);
    const downloadRow = document.getElementById("row" + id);
    if (status == "Recoding") {
        progressBar.style.width = 100 + '%';
    } else {
        // console.log("progress ")
        progressBar.style.width = status;
    }
    porcenShell.innerHTML = status;

}

function checkStatusRow(id, downloaded, status) {
    console.log("CHECK STATUS! " + id)
    console.log("downloaded --> " + downloaded)
    console.log("Status -> " + status)

    const downloadRow = document.getElementById("row" + id);
    const progressBar = document.getElementById("progressBar" + id);
    const porcenShell = document.getElementById("porcenShell" + id);
    if (downloaded == true) {
        downloadRow.style.backgroundColor = GREEN_STATUS;
        progressBar.style.width = 100 + '%';
        console.log(status)
    }
}

