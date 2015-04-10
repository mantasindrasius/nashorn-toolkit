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

    runner.on('start', function(){
        reporter.start(total);
    });

    runner.on('test', function(test){
        reporter.startTest(test.title, test.fullTitle());
    });

    runner.on('pass', function(test){
        reporter.pass(test.title, test.fullTitle(), test.duration);
    });

    runner.on('pending', function(test){
        reporter.pending(test.title, test.fullTitle());
    });

    runner.on('fail', function(test, err){
        print(JSON.stringify(err));
        reporter.fail(test.title, test.fullTitle(), test.duration, err.message);
    });

    runner.on('end', function(){
        var st = self.stats;

        reporter.end(st.suites, st.tests, st.passes, st.pending, st.failures, st.duration);
    });
}

mocha.reporter(NashornReporter);
mocha.setup('bdd');

mocha;