
* убрать ; на концах строк

* @Override заменить на override

* использовать def для определения метода

* использовать = для отделения имени метода от его тела

* использовать var/val для определния переменных и констант

* проверить и убрать все checked исключения

* проверить что все if-ы имеют else ветки

* использовать require:
  - Validate.isTrue(dto != null, "DTO must be non null");
  + require(dto != null, "DTO must be non null")

* использовать трейт Versionable/Identifiable/Auditable
  (created/updated) для сущностей

* использовать @scala.throws для обозначения того какие исключения возбуждает метод (?)

