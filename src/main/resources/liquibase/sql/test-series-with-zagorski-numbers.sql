INSERT INTO zagorski_catalog(code) VALUES('83');
INSERT INTO series_zagorski_catalog(series_id, zagorski_id) SELECT 1, id FROM zagorski_catalog WHERE code = '83';
