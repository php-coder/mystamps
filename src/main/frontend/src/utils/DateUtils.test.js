import './DateUtils.js'

describe('DateUtils.formatDateToDdMmYyyy()', () => {
	
	it('should return a string in a format dd.mm.yyyy', () => {
		const date = new Date('2018-06-12T16:59:54.451Z');
		expect(DateUtils.formatDateToDdMmYyyy(date)).toEqual('12.06.2018');
	});
	
});
