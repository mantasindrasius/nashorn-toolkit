(function(global) {
    function TimeoutTask(fn) {
        var self = this;

        self._cancelled = false;
        self._handle = null;

        this.exec = function () {
            if (!self._cancelled) fn();
        };

        this.cancel = function () {
            self._cancelled = true;
            if (self._handle) self._handle.cancel();
        };

        this.withHandle = function (handle) {
            self._handle = handle;
            return self;
        };
    }

    function scheduleTask(method, fn, millis) {
        var task = new TimeoutTask(fn);
        var handle = method(task.exec, millis);

        return task.withHandle(handle);
    }

    function clearTask(task) {
        if (task != undefined && task.cancel) task.cancel();
    }

    global.setTimeout = function (fn, millis) {
        return scheduleTask(function(exec, millis) {
            return EventLoop.schedule(exec, millis);
        }, fn, millis);
    };

    global.setInterval = function (fn, millis) {
        return scheduleTask(function(exec, millis) {
            return EventLoop.repeat(exec, millis);
        }, fn, millis);
    };

    global.clearTimeout = clearTask;
    global.clearInterval = clearTask;
})(this);