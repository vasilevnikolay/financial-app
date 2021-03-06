package third;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.math3.distribution.NormalDistribution;

@SuppressWarnings("serial")
public class ECallServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{
		
		req.getRequestDispatcher("/jsp/ecall.jsp").forward(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{
		
		double s,r,sigma,k,T;
		resp.setContentType("text/html;charset=UTF-8");
		try{
			s = Double.parseDouble(req.getParameter("spot"));
			r = Double.parseDouble(req.getParameter("interest"));
			sigma = Double.parseDouble(req.getParameter("vol"));
			k = Double.parseDouble(req.getParameter("strike"));
			T = Double.parseDouble(req.getParameter("horizon"))/365;
		}
		catch(NumberFormatException e){
			req.setAttribute("error", " Invalid input");
			req.getRequestDispatcher("/jsp/ecall.jsp").forward(req, resp);
			return;
		}
		
		if(s <= 0 || sigma <= 0 || k <= 0 || T <= 0){
			req.setAttribute("error", " Invalid input");
			req.getRequestDispatcher("/jsp/ecall.jsp").forward(req, resp);
			return;
		}			
		
		double d1 = 1/(sigma*Math.sqrt(T))*(Math.log(s/k) + (r + sigma*sigma/2)*T);
		double d2 = d1 - sigma*Math.sqrt(T);
		NormalDistribution Z = new NormalDistribution();
		double ecallValue = Z.cumulativeProbability(d1)*s - Z.cumulativeProbability(d2)*k*Math.pow(Math.E, -r*T);
		//round off to the nearest hundredth
		ecallValue = (new BigDecimal(ecallValue)).setScale(2, RoundingMode.HALF_UP).doubleValue();

        req.setAttribute("call", "Call value: " + ecallValue);        
        req.getRequestDispatcher("/jsp/ecall.jsp").forward(req, resp);
	}
}
