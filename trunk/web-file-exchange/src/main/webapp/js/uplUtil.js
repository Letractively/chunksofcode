// encoding: UTF-8


/***********************************************
 * a small set of common html routines
 ***********************************************/


if ( ! this.upl) {
    this.upl = {};
}
if ( ! this.upl.Util) {
    this.upl.Util = {};
}


/**
 * calculate the current context root without a trailing slash e.g.
 * 
 * @returns something like: "http://nasenhost:12345/fritz-die-app"
 */
upl.Util.getContextPath = function() {
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


/**
 * registers a callbackfunction to a element.
 * 
 * @param target
 *            the dom element where we want to register the event handler
 * @param eventhandler
 *            the name of the event, e.g. 'click' for onclick()
 * @param callbackfunction
 *            the function that will handle the event
 */
upl.Util.registerEventHandler = function(target, eventhandler, callbackfunction) {
    if (target.addEventListener) {
        target.addEventListener(eventhandler, callbackfunction, false);
    } else {
        target['on' + eventhandler] = callbackfunction;
    }
};


/**
 * removes all children of an element 
 */
upl.Util.clearChildren = function(targetElement) {
    while (targetElement.firstChild) {
        targetElement.removeChild(targetElement.firstChild);
    }
};


/**
 * answers the 'body' element of an iframe object
 */
upl.Util.getIframeBody = function(iframe) {
    var iFrameBody = null;
    
    if (iframe.contentDocument) { // FF
      iFrameBody = iframe.contentDocument.getElementsByTagName('body')[0];
    
    } else if (iframe.contentWindow) { // IE
      iFrameBody = iframe.contentWindow.document.getElementsByTagName('body')[0];
    
    } else { 
        alert('ERROR: could not determine iFrameBody in this browser!');
        // TODO: maybe raise a warning here.
    }
    
    return iFrameBody;
};