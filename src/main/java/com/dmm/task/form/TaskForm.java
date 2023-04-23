package com.dmm.task.form;

import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class TaskForm {
	public TaskForm(String title2, String text2) {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	// titleへのバリデーション設定を追加
	@Size(min = 1, max = 200)
	private String title;
	// textへのバリデーション設定を追加
	@Size(min = 1, max = 200)
	private String text;
}