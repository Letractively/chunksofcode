if (!this.$) {
	this.$ = function $(id) { 
		return document.getElementById(id);
	};
}

if (!this.ct) {
	this.ct = {};
}

ct.attachEvent = function(obj, event, callback) {
  if (obj.addEventListener)
    obj.addEventListener(event, callback, false);
  else
    obj['on' + event] = callback;
};

ct.clearSubtree = function(node) {
  while (node.firstChild)
    node.removeChild(node.firstChild);
};
