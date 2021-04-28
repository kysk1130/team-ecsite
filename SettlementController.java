package jp.co.internous.sugar.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.sugar.model.domain.MstDestination;
import jp.co.internous.sugar.model.mapper.MstDestinationMapper;
import jp.co.internous.sugar.model.mapper.TblCartMapper;
import jp.co.internous.sugar.model.mapper.TblPurchaseHistoryMapper;
import jp.co.internous.sugar.model.session.LoginSession;

@Controller
@RequestMapping("/sugar/settlement")
public class SettlementController {

	@Autowired
	private LoginSession loginSession;
	
	@Autowired
	TblCartMapper tblCartMapper;
	
	@Autowired
	MstDestinationMapper mstDestinationMapper;
	
	@Autowired
	TblPurchaseHistoryMapper tblPurchaseHistoryMapper;
	
	private Gson gson = new Gson();
	
	@RequestMapping("/")
	public String settlement(Model m) {
		int userId = loginSession.getUserId();
		
		List<MstDestination> mst_destination = mstDestinationMapper.findByUserId(userId);
		m.addAttribute("mst_destination", mst_destination);
		m.addAttribute("loginSession",loginSession);
		
		return "settlement";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/complete")
	@ResponseBody
	public boolean complete(@RequestBody String destinationId) {
		Map<String, String> map = gson.fromJson(destinationId, Map.class);
		String id = map.get("destinationId");
		
		int userId = loginSession.getUserId();
		Map<String, Object> parameter = new HashMap<>();
		parameter.put("destinationId", id);
		parameter.put("userId", userId);
		int insertCount = tblPurchaseHistoryMapper.insert(parameter);
		
		int deleteCount = 0;
		if (insertCount > 0) {
			deleteCount = tblCartMapper.deleteByUserId(userId);
		}
		return deleteCount == insertCount;
	}
}
