function _TimeoutTask(cb) {
    var self = this;

    this.cancelled = false;

    this.run = function() {
        if (!self.cancelled) cb();
    };

    this.cancel = function() {
        self.cancelled = true;
    }
}

this.setTimeout = function(fn, millis) {
    var task = new _TimeoutTask(fn);

    EventLoop.schedule(function() {
        task.run();
    }, millis);

    return task;
};

this.clearTimeout = function(task) {
    if (task != undefined && task.cancel) task.cancel();
};