var expect = chai.expect;

describe("suite 1", function() {
    it("should pass", function () {
        expect(1).to.be.equal(1);
    });

    it("should fail", function () {
        expect(1).to.be.equal(2);
    });

    xit("should ignore", function () {
        expect(1).to.be.equal(2);
    });
});