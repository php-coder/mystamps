INSERT INTO solovyov_catalog(code) VALUES('77');
INSERT INTO series_solovyov_catalog(series_id, solovyov_id) SELECT 1, id FROM solovyov_catalog WHERE code = '77';
