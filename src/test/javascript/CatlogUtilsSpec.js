describe("CatalogUtils.expandNumbers()", function() {
	
	it("should return empty string for empty string", function() {
		expect(CatalogUtils.expandNumbers("")).toBe("");
	});
	
	it("should return null for null", function() {
		expect(CatalogUtils.expandNumbers(null)).toBeNull();
	});
	
	it("should return undefined for empty unndefined", function() {
		expect(CatalogUtils.expandNumbers(undefined)).toBeUndefined();
	});
	
	it("should return string without hyphen as is", function() {
		expect(CatalogUtils.expandNumbers("test")).toEqual("test");
	});
	
	it("should return 'one-two' for 'one-two'", function() {
		expect(CatalogUtils.expandNumbers("one-two")).toEqual("one-two");
	});
	
	it("should return '1,2' for '1-2'", function() {
		expect(CatalogUtils.expandNumbers("1-2")).toEqual("1,2");
	});
	
	it("should return '1,2,3' for '1-3'", function() {
		expect(CatalogUtils.expandNumbers("1-3")).toEqual("1,2,3");
	});
	
	it("should return '10,11' for '10-11'", function() {
		expect(CatalogUtils.expandNumbers("10-11")).toEqual("10,11");
	});
	
	it("should return '10,11,12' for '10-12'", function() {
		expect(CatalogUtils.expandNumbers("10-12")).toEqual("10,11,12");
	});
	
	it("should return '2415,2416,2417' for '2415-7'", function() {
		expect(CatalogUtils.expandNumbers("2415-7")).toEqual("2415,2416,2417");
	});
	
	it("should return '2415,2416,2417' for '2415-17'", function() {
		expect(CatalogUtils.expandNumbers("2415-17")).toEqual("2415,2416,2417");
	});
	
	it("should return '2499,2450,2451' for '2499-501'", function() {
		expect(CatalogUtils.expandNumbers("2499-501")).toEqual("2499,2500,2501");
	});
	
	it("should return '1,2,3,5' for '1-3,5'", function() {
		expect(CatalogUtils.expandNumbers("1-3,5")).toEqual("1,2,3,5");
	});
	
	it("should return '1,2,3,5,6' for '1-3,5-6'", function() {
		expect(CatalogUtils.expandNumbers("1-3,5-6")).toEqual("1,2,3,5,6");
	});
	
	it("should return '1,2,3,5,6,8,9' for '1-3,5-6,8-9'", function() {
		expect(CatalogUtils.expandNumbers("1-3,5-6,8-9")).toEqual("1,2,3,5,6,8,9");
	});
	
	it("should return 'test 1-3' for 'test 1-3'", function() {
		expect(CatalogUtils.expandNumbers("test 1-3")).toEqual("test 1-3");
	});
	
	it("should return '09-5' for '09-5'", function() {
		expect(CatalogUtils.expandNumbers("09-5")).toEqual("09-5");
	});
	
	it("should return '854,855,856,146' for '854-856, 146'", function() {
		expect(CatalogUtils.expandNumbers("854-856, 146")).toEqual("854,855,856,146");
	});
	
	it("should return '4596,4597,4598' for ' 4596-8'", function() {
		expect(CatalogUtils.expandNumbers(" 4596-8")).toEqual("4596,4597,4598");
	});
	
	it("should return '4596,4597,4598' for '4596-8 '", function() {
		expect(CatalogUtils.expandNumbers("4596-8 ")).toEqual("4596,4597,4598");
	});
	
	it("should return '1,2,3' for '1/3'", function() {
		expect(CatalogUtils.expandNumbers("1/3")).toEqual("1,2,3");
	});
	
	it("should return '1,2,3,4,5,6' for '1/3, 4-6'", function() {
		expect(CatalogUtils.expandNumbers("1/3, 4-6")).toEqual("1,2,3,4,5,6");
	});
	
});
