package com.ppdai.das.console.dto.entry.codeGen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class W2uiElement {
	
	private String id;
	
	private boolean children;
	
	private String data;
	
	private String text;
	
	private String type;

}
