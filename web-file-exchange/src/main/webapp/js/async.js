//-*- coding: utf8 -*-
//Copyright (c) 2009-2010 Oliver Lau <ola@ct.de>, Heise Zeitschriften Verlag

if (!this.ct) this.ct = {};

ct.async = {};

ct.async.XHR = function(url, params, opts) {
	if (opts == null || typeof(opts) === 'undefined')
		opts = {};
	opts.method = (!opts.method)? 'POST' : opts.method.toUpperCase();
	opts.returntype = (!opts.returntype)? 'XML' : opts.returntype.toUpperCase();
	if (!opts.async) opts.async = true;
	var uriParams = '';
	if (params)
		for (p in params)
			uriParams += p + '=' + params[p] + '&';
	var xhr = null;
	try {
		xhr = new XMLHttpRequest();
	} catch(e) {
		try {
			// MS Internet Explorer (ab v5)
			xhr  = new ActiveXObject('Msxml2.XMLHTTP');
		} catch(e) {
			xhr  = null;
		}
	}
	if (xhr) {
		if (opts.method == 'GET' && params) {
			url += '?' + uriParams;
			// Caching Proxies austricksen
			url += '_x=' + Math.random() + (new Date()).getTime();
			uriParams = null;
		}
		xhr.open(opts.method, url, opts.async);
		if (opts.method == 'POST')
			xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
		if (opts.readyCallback) {
			xhr.onreadystatechange = function() {
				if (xhr.readyState == 4 && xhr.status == 200) {
					switch(opts.returntype) {
					case 'XML':
						opts.readyCallback(xhr.responseXML);
						break;
					case 'JSON':
						opts.readyCallback(JSON.parse(xhr.responseText));
						break;
					default:
						throw 'Unknown return type: "' + opts.returntype + '"';
						break;
					}
				}
			};
		}
		xhr.onprogress = opts.progressCallback;
		xhr.onerror = function() { alert('XHR failed'); };
		xhr.send(uriParams);
		if (!opts.async && xhr.status == 200) {
			switch(opts.returntype) {
			case 'XML':
				return xhr.responseXML;
			case 'JSON':
				return JSON.parse(xhr.responseText);
			default:
				throw 'Unknown return type: "' + opts.returntype + '"';
				break;
			}
		}
	}
	return null;
};

ct.async.jsonRequest = function(url, params, opts) {
	if (opts == null || typeof(opts) === 'undefined')
		opts = {};
	opts.returntype = 'JSON';
	ct.async.XHR(url, params, opts);
};

ct.async.xmlRequest = function(url, params, opts) {
	if (opts == null || typeof(opts) === 'undefined')
		opts = {};
	opts.returntype = 'XML';
	ct.async.XHR(url, params, opts);
};
