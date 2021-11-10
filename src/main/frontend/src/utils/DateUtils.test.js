import './DateUtils.js'

describe('DateUtils.formatDateToDdMmYyyy()', () => {
	
	it('should return a string in a format dd.mm.yyyy', () => {
		// given
		const date = new Date('2018-06-12T16:59:54.451Z');
		// when
		const result = DateUtils.formatDateToDdMmYyyy(date);
		// then
		expect(result).toBe('12.06.2018');
	});
	
});
