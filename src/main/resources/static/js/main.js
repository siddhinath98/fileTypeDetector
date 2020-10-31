'use strict';

const singleUploadForm = document.querySelector('#singleUploadForm');
const singleFileUploadInput = document.querySelector('#singleFileUploadInput');
const singleFileUploadError = document.querySelector('#singleFileUploadError');
const singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');
const resultShow = document.querySelector('#resultShow');

function uploadSingleFile(file) {
    let formData = new FormData();
    formData.append("file", file);

    let xhr = new XMLHttpRequest();
    xhr.open("POST", "/uploadFile");

    xhr.onload = function() {
        console.log(xhr.responseText);
        let response = JSON.parse(xhr.responseText);
        if(xhr.status == 200) {
            singleFileUploadError.style.display = "none";
            singleFileUploadSuccess.innerHTML = "<p>File Uploaded Successfully.</p><br><p>Download_Logs : <a href='" + response.fileDownloadUri + "' target='_blank'>Click here to download</a></p>";
            singleFileUploadSuccess.style.display = "block";

            fetch(response.fileDownloadUri)
                .then(response => response.text()
                .then(data => {
                    console.log(data)
                    const temp = data.replace(/(?:\r\n|\r|\n)/g, '<br>')
                    resultShow.innerHTML = "<p class=\"borderexample\">"+temp+"</p>"
                }));
        } else {
            singleFileUploadSuccess.style.display = "none";
            singleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
        }
    }

    xhr.send(formData);
}

singleUploadForm.addEventListener('submit', function(event){
    let files = singleFileUploadInput.files;
    if(files.length === 0) {
        singleFileUploadError.innerHTML = "Please select a file";
        singleFileUploadError.style.display = "block";
    }
    uploadSingleFile(files[0]);
    event.preventDefault();
}, true);


