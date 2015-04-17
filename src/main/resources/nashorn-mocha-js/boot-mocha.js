location = {
    search: ''
};

process = {
    stdout: {
        write: function(text) {
            print(text);
        }
    }
};

console = {
    log: print
};

function NashornReporter(runner) {
    Mocha.reporters.Base.call(this, runner);

    var self = this
        , stats = this.stats
        , total = runner.total;

    runner.on('start', function() {
        reporter.started(total);
    });

    runner.on('suite', function(suite) {
        var title = suite.title.trim();

        if (title.length)
            reporter.suiteStarted(title);
    });

    runner.on('suite end', function(suite) {
        var title = suite.title.trim();

        if (title.length)
            reporter.suiteFinished(title);
    });

    runner.on('test', function(test){
        reporter.testStarted(test.title, test.fullTitle());
    });

    runner.on('pass', function(test){
        reporter.testPassed(test.title, test.fullTitle(), test.duration);
    });

    runner.on('pending', function(test){
        reporter.testIgnored(test.title, test.fullTitle());
    });

    runner.on('fail', function(test, err){
        print(JSON.stringify(err));
        reporter.testFailed(test.title, test.fullTitle(), test.duration, err.message);
    });

    runner.on('end', function(){
        var st = self.stats;

        reporter.finished();
        //reporter.end(st.suites, st.tests, st.passes, st.pending, st.failures, st.duration);
    });
}

mocha.reporter(NashornReporter);
mocha.setup('bdd');

mocha;