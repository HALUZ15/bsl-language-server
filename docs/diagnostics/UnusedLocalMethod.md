# Неиспользуемый локальный метод (UnusedLocalMethod)

|      Тип      |    Поддерживаются<br>языки    | Важность |    Включена<br>по умолчанию    |    Время на<br>исправление (мин)    |                        Теги                        |
|:-------------:|:-----------------------------:|:--------:|:------------------------------:|:-----------------------------------:|:--------------------------------------------------:|
| `Дефект кода` |         `BSL`<br>`OS`         | `Важный` |              `Да`              |                 `1`                 |       `standard`<br>`suspicious`<br>`unused`       |

## Параметры


|            Имя             |   Тип    |                    Описание                     |    Значение<br>по умолчанию    |
|:--------------------------:|:--------:|:-----------------------------------------------:|:------------------------------:|
| `attachableMethodPrefixes` | `Строка` | `Префиксы подключаемых методов (через запятую)` |  `подключаемый_,attachable_`   |
<!-- Блоки выше заполняются автоматически, не трогать -->
## Описание диагностики

Программные модули не должны иметь неиспользуемых процедур и функций. Диагностика умеет пропускать `подключаемые методы`, имеющие префиксы, указанные в параметре диагностики. 

## Источники

* [Стандарт: Тексты модулей](https://its.1c.ru/db/v8std#content:456:hdoc)

## Сниппеты

<!-- Блоки ниже заполняются автоматически, не трогать -->
### Экранирование кода

```bsl
// BSLLS:UnusedLocalMethod-off
// BSLLS:UnusedLocalMethod-on
```

### Параметр конфигурационного файла

```json
"UnusedLocalMethod": {
    "attachableMethodPrefixes": "подключаемый_,attachable_"
}
```
