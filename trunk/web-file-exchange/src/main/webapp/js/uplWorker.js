// encoding: UTF-8

/**
 * 
 * A Worker encapsulates one single file upload.
 * Worker instances are created by the Manager.<br>
 *
 * An upload may be running, finished or queued.
 * 
 * Each worker has:<br>
 *  1. A form element with a file input element<br>
 *  2. A hidden iframe element that acts as receiver
 *     for the upload response<br>
 *  3. A list item that represents the status of
 *     the worker item in the document's fileList<br>
 *     
 */



if ( ! this.upl) {
    this.upl = {};
}


upl.Worker = function() {
    
    /**************** DOM ELEMENTS ********************/
	
    var form = document.createElement('form');
    var input = document.createElement('input');
    var hiddenIframe = document.createElement('iframe');
    var listItem = document.createElement('li');
    
    /**************** INSTANCE FIELDS ********************/
    
    // part of all element-ids generated by this worker
    this.id = upl.Manager.getNextWorkerId();
    
    // flag to remember if this upload is running
    this.uplodadRunning = false;
    
    // where we choose a file with
    this.input = input;
    
    // submits the file to the web server
    this.form = form;
    
    // where the upload response will be written to
    this.hiddenIframe = hiddenIframe;
    
    // represents this upload in the web pages fileList
    this.listItem = listItem;
    
    
    //  upload form  
    form.id = 'uploadForm-' + this.id;
    form.method = 'POST';
    form.target = 'uploadTarget-' + this.id;
    form.enctype = 'multipart/form-data';
    form.action = upl.Manager.uploadUrl;
    
    
    //   file input  
    input.type = 'file';
    input.name = 'file';
    input.id = 'fileInput-' + this.id;
    var _this = this;
    upl.Util.registerEventHandler(input, 'change', function() {
        _this.upload(true); // upload immediately after a file was selected.
    });
    form.appendChild(input);
    
    
    // tell uploadservlet the manager's fingerprint 
    var managerFingerInput = document.createElement('input');
    managerFingerInput.id = 'client-fingerprint';
    managerFingerInput.name = 'client-fingerprint';
    managerFingerInput.type = 'hidden';
    managerFingerInput.value = upl.Manager.fingerprint;
    form.appendChild(managerFingerInput);
    
    
    // tell uploadservlet the worker's id 
    var workerIdInput = document.createElement('input');
    workerIdInput.id = 'workerId';
    workerIdInput.name = 'workerId';
    workerIdInput.type = 'hidden';
    workerIdInput.value = this.id;
    form.appendChild(workerIdInput);
    
    
    // hidden iframe, where the response will be written to  
    hiddenIframe.id = 'uploadTarget-' + this.id;
    hiddenIframe.name = 'uploadTarget-' + this.id;
    hiddenIframe.setAttribute('style', 'position:absolute; left:-1000px;');
    
    
    // list item, where the state of this worker is displayed 
    listItem.id = 'fileList-item-' + this.id; // processed in upload()
    console.debug("worker-"+this.id+" created!");
};


/**
 * submits the upload form or puts it into the upload queue.<br>
 * 
 * called by the onchange() handler of the upload-form's file element (just
 * after a file had been selected) or by the startNextDownload() method of the
 * Worker.<br>
 * 
 * when a free upload slot is available, this will submit the upload-form.<br>
 * if not, it will be added to the upload queue.<br>
 * 
 * @param appendToList
 *            if true, the uploader instance's listItem will be added to the
 *            fileList DOM element
 */
upl.Worker.prototype.upload = function(appendToList) {
    if (appendToList) {
        upl.Manager.addFileToList(this.listItem);
        
        // hide the form element
        this.form.setAttribute('style', 'position:absolute; left:-1000px;');

        // tell manager to add a new worker for next upload
        upl.Manager.insertNewWorker();
    }
    
    if ( ! upl.Manager.isUploadSlotAvailable()) {
        this.updateListItem("icon.upload.queued.png", 'In Upload queue.');
        console.debug("worker-"+this.id+" upload of: "+this.input.value+" queued.");
        return;
    }
    
    // OK, WE HAVE A FREE UPLOAD SLOT, START TRANSFER!
    
    // register onload handler (called when the upload response was loaded)
    var _this = this;
    upl.Util.registerEventHandler(this.hiddenIframe, 'load', function() {
    	_this.uplodadRunning = false;
        // check for the upload status inside the response body.
        var iFrameBody = upl.Util.getIframeBody(_this.hiddenIframe);
        
        if (/UPLOAD OK/i.test(iFrameBody.innerHTML)) { // success
            _this.uploadCompleted();
        } else {
            var msg = iFrameBody.innerHTML.replace(/^.* MESSAGE=([^<]*)/, "$1");
            _this.uploadCompleted(msg); 
        }
    });

    // submit upload form, and set the "upload-in-progress-image":
    this.uplodadRunning = true;

    console.debug("worker-"+this.id+" starting upload of: "+this.input.value);
    this.form.submit();
    this.updateListItem("icon.upload.loading.gif", 'Uploading...');
};


/** answers whether or not this upload is ready to submit. */
upl.Worker.prototype.mayStartUploading = function() {
    var result = ( ! this.uplodadRunning && this.input.value.length > 0);
    return result;    
};


/**
 * called from the form.onload() callback after the file upload was
 * finished. set a new icon to the upload. calls startNextDownload() to start
 * next remaining download.
 * 
 * @param errorMsg
 *            the error message, if the download failed. else null.
 */
upl.Worker.prototype.uploadCompleted = function(errorMsg) {
    console.debug("worker-"+this.id+" finished upload of: "+this.input.value);
    
    if ( ! errorMsg) { // upload was OK
        this.updateListItem("icon.upload.success.png", 'Upload successful.');
    } else {
        this.updateListItem("icon.upload.error.png", 'Upload failed.', errorMsg);
    }
    
    this.collectGarbage();
    upl.Manager.startNextDownload();
};


/**
 * update the appearance of the list item for this upload: set the specified
 * image to the entry, and append an error message if given.
 */
upl.Worker.prototype.updateListItem = function(imgName, imgTitle, errorMessage) {
    var imgElement = document.createElement('img');
    imgElement.src = upl.Util.getContextPath()+'/' + imgName;
    imgElement.title = imgTitle;
    imgElement.setAttribute('style', 'width: 16x; height: 16px;');
    
    var li = this.listItem;
    var fileName = this.input.value;
    upl.Util.clearChildren(li);
    li.appendChild(imgElement);
    li.appendChild(document.createTextNode(' ' + fileName));
    
    if (errorMessage) {
        var errorSpan = document.createElement('span');
        errorSpan.setAttribute('style', 'color: red; font-size: 10pt;');
        errorSpan.innerHTML = '<br/>'+errorMessage;
        li.appendChild(errorSpan);
    }
}


/** free resources after upload has finished. */
upl.Worker.prototype.collectGarbage = function() {
	var _id = this.id;
    // do NOT remove obj.listItem, it will remain in fileList !
    this.form.parentNode.removeChild(this.form);
    this.hiddenIframe.parentNode.removeChild(this.hiddenIframe);
    upl.Manager.removeOldWorker(this.id);
    var obj = this;
    delete obj;
    console.debug("worker-"+_id+" destroyed.");
};