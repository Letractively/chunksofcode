

////-*- coding: utf8 -*-

/**
 * returns the current context root without a trailing slash
 * e.g. http://nasenhost:12345/fritz-die-app 
 */
function getContextPath() {
	// calculate the upload target path:
	
	// 1.) get the current url:
	var base = document.getElementsByTagName('base')[0];
	if (base && base.href && (base.href.length > 0)) {
		base = base.href;
	} else {
		base = document.URL;
	}
	
	// 2.) get the context path:
	var protocolEnd = base.indexOf("//");
	var domainNameEnd = base.indexOf("/", protocolEnd + 2);
	var contextRootEnd = base.indexOf("/", domainNameEnd + 1);
	var contextPath = base.substr(0, contextRootEnd);
	
	return contextPath;
}




////Copyright (c) 2009-2010 Oliver Lau <ola@ct.de>, Heise Zeitschriften Verlag


if ( ! this.ct) this.ct = {};

ct.Uploader = function(attrs) {
	if (attrs == null || typeof(attrs) === 'undefined')
		attrs = {};
	if (attrs.onProgress != null && typeof(attrs.onProgress) !== 'function')
		throw 'Attribute onProgress must be a function!';
	this.onProgressHandler = attrs.onProgress;
	this.uploadUrl = ct.Uploader.uploadUrl;
	this.id = ct.Uploader.getId();
	this.inProgress = false;
	this.form = document.createElement('form');
	this.form.id = 'upload_form_' + this.id;
	this.form.method = 'POST';
	this.form.target = 'upload_target_' + this.id;
	this.form.enctype = 'multipart/form-data';
	this.form.action = this.uploadUrl;
	this.input = document.createElement('input');
	this.input.name = 'file';
	this.input.type = 'file';
	this.input.id = 'file_selector_' + this.id;
	var that = this;
	ct.attachEvent(this.input, 'change', function() { that.upload(true); });
	this.form.appendChild(this.input);
	this.infoElement = document.createElement('div');
	this.infoElement.innerHTML = (ct.Uploader.firstUpload && this.uploadUrl == null)
	? ct.Uploader.loaderIconImgHtml + ' Calculating Upload-URL...&nbsp;&hellip;'
			: '<span class="status">Note, the upload will start immediately after file selection!</span>';
	this.form.appendChild(this.infoElement);
	ct.Uploader.formContainer.appendChild(this.form);
	this.iframe = document.createElement('iframe');
	this.iframe.id = 'upload_target_' + this.id;
	this.iframe.name = 'upload_target_' + this.id;
	this.iframe.setAttribute('style', 'position:absolute; left:-1000px;');
	ct.Uploader.formContainer.appendChild(this.iframe);
	if (this.uploadUrl == null) {
		if (ct.Uploader.firstUpload) {
			ct.Uploader.firstUpload = false;
			this.input.disabled = true;
		}
		this.prefetchUrls();
	}
};

//Konstanten
ct.Uploader.DEFAULT_MAX_PARALLEL_UPLOADS = 4;
ct.Uploader.DEFAULT_MAX_PREFETCHED_URLS = 2 * ct.Uploader.DEFAULT_MAX_PARALLEL_UPLOADS;

//statische Variablen
ct.Uploader.maxParallelUploads = ct.Uploader.DEFAULT_MAX_PARALLEL_UPLOADS;
ct.Uploader.firstUpload = true;
ct.Uploader.uploads = [];
ct.Uploader.prefetchedUrls = [];
ct.Uploader.maxPrefetchedUrls = ct.Uploader.DEFAULT_MAX_PREFETCHED_URLS;
ct.Uploader.uploadUrl = null;

ct.Uploader.init = function(attrs) {
	if (attrs.formContainer == null || typeof(attrs.formContainer) !== 'object')
		throw 'Attribute formContainer missing!';
	if (attrs.listTarget == null || typeof(attrs.listTarget) !== 'object')
		throw 'Attribute listTarget missing!';
	if (attrs.loaderIconImgHtml != null && typeof(attrs.loaderIconImgHtml) !== 'string')
		throw 'Attribute loaderIconImgHtml must be a String!';
	if (attrs.uploadUrl != null && typeof(attrs.uploadUrl) !== 'string')
		throw 'Attribute uploadUrl must be a string!';
	if (attrs.maxParallelUploads != null && typeof(attrs.maxParallelUploads) !== 'number')
		throw 'Attribute maxParallelUploads must be a number!';
	ct.Uploader.formContainer = attrs.formContainer;
	ct.Uploader.listTarget = attrs.listTarget;
	ct.Uploader.loaderIconImgHtml = attrs.loaderIconImgHtml || '<img style="border: 1px solid #333" src="'+getContextPath()+'/loadericon.gif" width="16" height="11" title="Please wait ...">';
	ct.Uploader.uploadUrl = attrs.uploadUrl;
	ct.Uploader.maxParallelUploads = (attrs.maxParallelUploads != null)? attrs.maxParallelUploads : ct.Uploader.DEFAULT_MAX_PARALLEL_UPLOADS;
	ct.Uploader.maxPrefetchedUrls = (attrs.maxPrefetchedUrls != null)? attrs.maxPrefetchedUrls : 2 * ct.Uploader.maxParallelUploads;
	ct.Uploader.create();
};

ct.Uploader.create = function() { 
	ct.Uploader.uploads.push(new ct.Uploader());
};

ct.Uploader.destroy = function(obj) {
	obj.form.parentNode.removeChild(obj.form);
	obj.iframe.parentNode.removeChild(obj.iframe);
	ct.Uploader.uploads[obj.id] = null;
	delete obj;
};

ct.Uploader.getId = function() {
	return ct.Uploader.uploads.length;
};

ct.Uploader.next = function() {
	for (var i = 0; i < ct.Uploader.uploads.length; ++i) {
		var u = ct.Uploader.uploads[i];
		if (u != null && u.isPending()) {
			u.upload(false);
			return;
		}
	}
};

ct.Uploader.numUploadsInQueue = function() {
	var n = 0;
	for (var i = 0; i < ct.Uploader.uploads.length; ++i)
		if (ct.Uploader.uploads[i] != null && ct.Uploader.uploads[i].inProgress)
			++n;
	return n;
};

ct.Uploader.prototype.prefetchUrls = function() {
	for (var i = ct.Uploader.prefetchedUrls.length; i < ct.Uploader.maxPrefetchedUrls; ++i)
		this.loadUrl();
};

ct.Uploader.prototype.isPending = function() {
	return !this.inProgress && this.input.value.length > 0;	
};

ct.Uploader.prototype.savePrefetchedUrl = function(url) {
	ct.Uploader.prefetchedUrls.push(url);
};

ct.Uploader.prototype.getUploadUrl = function() {
  var url = this.uploadUrl;
  if (url == null) {
    url = ct.Uploader.prefetchedUrls.shift();
    this.prefetchUrls();
  }
  return url;
};

ct.Uploader.prototype.onUrlLoaded = function(response) {
	if (response && response.upload_url) {
		this.savePrefetchedUrl(response.upload_url);
		this.input.disabled = false;
		this.infoElement.innerHTML = '<span class="status">Note, the upload will start immediately after file selection!</span>';
	}
	else {
		alert('Could not fetch upload-URL!');
	}
};

ct.Uploader.prototype.loadUrl = function() {
	var that = this;
	ct.async.jsonRequest('/getuploadurl', {}, {
		readyCallback: function(res) { that.onUrlLoaded(res); },
		method: 'GET'
	});
};

ct.Uploader.prototype.onUploaded = function() {
	var accepted = document.createElement('img');
	accepted.src = getContextPath()+'/accept-icon.png';
	accepted.title = 'Uploaded successuflly.';
	accepted.setAttribute('style', 'width:16px;height:16px;');
	this.selectedFile.replaceChild(accepted, this.selectedFile.firstChild);
	ct.Uploader.destroy(this);
	ct.Uploader.next();
};

ct.Uploader.prototype.upload = function(appendToList) {
	if (appendToList) {
		this.selectedFile = document.createElement('div');
		this.selectedFile.id = 'selected_file_' + this.id;
		ct.Uploader.listTarget.appendChild(this.selectedFile);
		this.form.setAttribute('style', 'position:absolute;left:-1000px;');
		ct.Uploader.create();
	}
	if (ct.Uploader.numUploadsInQueue() < ct.Uploader.maxParallelUploads) {
		var that = this;
		ct.attachEvent(this.iframe, 'load', function() { that.onUploaded(); });
		this.inProgress = true;
		this.form.action = this.getUploadUrl();
		this.form.submit();
		this.selectedFile.innerHTML = ct.Uploader.loaderIconImgHtml + ' ' + this.input.value;
	}
	else {
		var scheduled = document.createElement('img');
		scheduled.src = getContextPath()+'/queue-icon.png';
		scheduled.title = 'In Upload queue.';
		scheduled.setAttribute('style', 'width:16px;height:16px;');
		this.selectedFile.appendChild(scheduled);
		this.selectedFile.appendChild(document.createTextNode(' ' + this.input.value));
	}
};