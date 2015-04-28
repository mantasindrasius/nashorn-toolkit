var expect = chai.expect;

describe("suite 1", function() {
    it("extra setup pass", function () {
        expect(new Whatever(1).value).to.be.equal(1);
    });
});