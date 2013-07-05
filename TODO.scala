
* использовать require:
  - Validate.isTrue(dto != null, "DTO must be non null");
  + require(dto != null, "DTO must be non null")

* использовать трейт Versionable/Identifiable/Auditable
  (created/updated) для сущностей

* использовать @scala.throws для обозначения того какие исключения возбуждает метод (?)

* использовать Option

* `ensuring` is used on a method's return value to check post-conditions

