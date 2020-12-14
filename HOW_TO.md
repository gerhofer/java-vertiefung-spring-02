# How to - Use a form 

Generell ist viel Info in verschiedensten Tutorials zu finden, z.B. https://www.baeldung.com/thymeleaf-in-spring-mvc
In Thymeleaf ist bei der Erstellung eines Formulars das Objekt wichtig, das erstellt werden sollte und die URL, die die Submission bekommt.
Ein Beispiel findest du im DemoController, DeomObject und demo.html

Ein exemplarisches Formular könnte so aussehen:
```xhtml
<form th:action="@{/saveEntity}" th:object="${myObject}" method="post">
    <table>
        <tr>
            <td><label>ID</label></td>
            <td><input type="number" th:field="*{id}" /></td>
        </tr>
        <tr>
            <td><label>Value</label></td>
            <td><input type="text" th:field="*{value}" /></td>
        </tr>
        <tr>
            <td><input type="submit" value="Submit" /></td>
        </tr>
    </table>
</form>
```
`th:action` spezifiert welcher Endpunkt unseres Webservers wir ansprechen wollen (in Kombination mit der HTTP Methode, die in `method=post` definiert wird)
D.h. damit unsere Form funktioniert brauchen wir einen Endpunkt: 
```java
@PostMapping("/saveEntity")
public void save(@RequestBody DemoObject demoObject) {
    // saving logic here
}
```

Der Inhalt von `th:object` `myObject` repräsentiert das Objekt, das dann das `@ModelAttribute` vom Request repräsentiert.
`th:field` repräsentiert die einzelnen Felder in unserem Objekt. Durch Verwendung der `*{value}` notation drücken wir aus, dass es in unserem Objekt, das wir in `th:object` angeben ein property `value` geben muss. 
DemoObject könnte dann also so aussehen:
```java 
public class DemoObject {

    private Long id;
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
```

Beim rendern der HTMl Form muss es dieses Objekt im `Model` geben!

```java
@GetMapping("/demo")
public ModelAndView coolForm() {
    Map<String, Object> myModel = new HashMap<>();
    myModel.put("myObject", new DemoObject());
    return new ModelAndView("demo", myModel);
}
```
## Formular Submit 

Der Submit button sendet die Daten aus dem Formular an den Server, je nachdem welche Action auf dem Formular spezifiziert wurde. 

## Validation 

Spring MVC stellt uns eine Datenstruktur für Validierungen zur Verfügung - das BindingResult. Mit dem können wir ganz einfach Validierungsmessages erstellen und ans frontend geben.

Im HTML brauchen wir dazu eine bedingte anzeige eines Fehlers: 
```xhtml
<tr>
    <td><label>Value</label></td>
    <td><input type="text" th:field="*{value}" /></td>
</tr>
<tr th:each="err : ${#fields.errors('value')}" th:text="${err}" />
```

Und im Controller können wir unter bestimmten Umständen einfach ein Feld als fehlerhaft markieren: 
```java
@PostMapping("/saveEntity")
public ModelAndView save(@ModelAttribute DemoObject myObject,
                         BindingResult bindingResult,
                         Model model) {
    if (myObject.getId() > 1000L) {
        bindingResult.rejectValue("id", "Id ist zu groß");
    }
    if (myObject.getValue().length() < 5) {
        bindingResult.rejectValue("value", "Value muss mindestens fünf Zeichen lang sein");
    }
    return new ModelAndView("demo", model.asMap())
}
```
Mit `#errors` wird auf die errors im BindingResult zugegriffen.

### I18n 

Ein ausführlicher Guide ist:
https://www.baeldung.com/spring-boot-internationalization

Im resources Ordner gibt es bereits zwei Dateien, eins für deutsche Übersetzungen und eins für englische. 
Dort gehören alle verwendeten Message definitionen eingetragen. Im Thymeleaf kann auf keys aus diesen Messages mit z.b `th:text="#{my.msg.key}"` zugegriffen werden.

### Database connection 

Um eine Datenbank anzubinden brauchen wir im `build.gradle` file zwei neue Abhängigkeiten.
```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
runtimeOnly 'mysql:mysql-connector-java'
```

Den connection String geben wir im `application.properties` an: 
```
spring.datasource.url=jdbc:mysql://localhost:3306/library?serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

Eine exemplarische DB Entity könnte so aussehen: 
```java 
@Table(name = "tableNameInDb")
@Entity
public class MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 2048)
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

```

Und ein Repository benötigen wir noch um auf die Tabelle zugreifen zu können: 
```
public interface MyEntityRepository extends JpaRepository<MyEntity, Long> {}
```