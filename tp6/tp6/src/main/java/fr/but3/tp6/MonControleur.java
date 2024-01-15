package fr.but3.tp6;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;

@RestController
class MonControleur
{
	@GetMapping("/")
	String home(HttpSession session)
	{
		session.setAttribute("dat", System.currentTimeMillis());
		return "OK!";
	}
}
