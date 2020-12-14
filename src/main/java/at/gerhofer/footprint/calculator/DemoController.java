package at.gerhofer.footprint.calculator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class DemoController {

    /**
     * Wenn wir die HTML Seite mit dem Formular ausliefern und in dem Formular ein th:object="${myObject}" verwenden
     * dann müssen wir dieses Objekt initialisieren und im Model mitgeben
     */
    @GetMapping("/demo")
    public ModelAndView coolForm() {
        Map<String, Object> myModel = new HashMap<>();
        myModel.put("myObject", new DemoObject());
        return new ModelAndView("demo", myModel);
    }

    @PostMapping("/saveEntity")
    public ModelAndView save(@ModelAttribute(name = "myObject") DemoObject myObject,
                             BindingResult bindingResult,
                             Model model) {
        if (myObject.getId() > 1000L) {
            bindingResult.rejectValue("id", "id.error");
        }
        if (myObject.getValue().length() < 5) {
            bindingResult.rejectValue("value", "value.error");
        }
        return new ModelAndView("demo", model.asMap());
    }

}
