package com.dmm.task.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.dmm.task.data.entity.Tasks;
import com.dmm.task.data.repository.TasksRepository;
import com.dmm.task.form.TaskForm;
import com.dmm.task.service.AccountUserDetails;

@Controller
public class TaskController {

	@Autowired
	private TasksRepository repo;

	/**
	 * 投稿の一覧表示.
	 * 
	 * @param model モデル
	 * @return 遷移先
	 */
	@GetMapping("/tasks")
	public String getCalendar(Model model) {
		// 逆順で投稿をすべて取得する
		List<Tasks> list = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		// Collections.reverse(list); //普通に取得してこちらの処理でもOK
		model.addAttribute("tasks", list);
		// 1. 2次元表になるので、ListのListを用意する
		List<List<LocalDate>> month = new ArrayList<>();

		// 2. 1週間分のLocalDateを格納するListを用意する
		List<LocalDate> week = new ArrayList<>();

		// 3. その月の1日のLocalDateを取得する
		LocalDate day = LocalDate.now();
		day = LocalDate.of(day.getYear(), day.getMonthValue(), 1);

		// 4.
		// 曜日を表すDayOfWeekを取得し、上で取得したLocalDateに曜日の値（DayOfWeek#getValue)をマイナスして前月分のLocalDateを求める
		DayOfWeek w = day.getDayOfWeek();
		day = day.minusDays(w.getValue() - 1); // これでdayには3/28が入る

		// 5. 1日ずつ増やしてLocalDateを求めていき、2．で作成したListへ格納していき、1週間分詰めたら1．のリストへ格納する
		for (int i = 1; i <= 7; i++) {
			week.add(day);
			day = day.plusDays(1);
		}
		month.add(week);
		week = new ArrayList<>(); // 次週分のリストを用意

		// 6.
		// 2週目以降は単純に1日ずつ日を増やしながらLocalDateを求めてListへ格納していき、土曜日になったら1．のリストへ格納して新しいListを生成する（月末を求めるにはLocalDate#lengthOfMonth()を使う）
		while (day.getMonthValue() == LocalDate.now().getMonthValue()) {
			week.add(day);
			if (day.getDayOfWeek() == DayOfWeek.SATURDAY) {
				month.add(week);
				week = new ArrayList<>();
			}
			day = day.plusDays(1);
		}

		// 7. 最終週の翌月分をDayOfWeekの値を使って計算し、6．で生成したリストへ格納し、最後に1．で生成したリストへ格納する
		if (!week.isEmpty()) {
			while (week.size() < 7) {
				week.add(day);
				day = day.plusDays(1);
			}
			month.add(week);
		}
		MultiValueMap<LocalDate, Tasks> tasks = new LinkedMultiValueMap<LocalDate, Tasks>();
		model.addAttribute("matrix", month);
		model.addAttribute("tasks", tasks);
		return "main";
	}

	/**
	 * 投稿を作成.
	 * 
	 * @param postForm 送信データ
	 * @param user     ユーザー情報
	 * @return 遷移先
	 */
	@PostMapping("/main/create")
	public String create(@Validated TaskForm taskForm, BindingResult bindingResult,
			@AuthenticationPrincipal AccountUserDetails user, Model model) {
		// バリデーションの結果、エラーがあるかどうかチェック
		if (bindingResult.hasErrors()) {
			// エラーがある場合は投稿登録画面を返す
			List<Tasks> list = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
			model.addAttribute("tasks", list);
			model.addAttribute("taskForm", taskForm);
			return "redirect:/tasks";
		}

		Tasks task = new Tasks();
		task.setName(user.getName());
		task.setTitle(taskForm.getTitle());
		task.setText(taskForm.getText());
		task.setDate(LocalDateTime.now());

		repo.save(task);

		return "redirect:/tasks";
	}

	/**
	 * タスクを追加.
	 * 
	 * @param taskForm      タスクデータ
	 * @param bindingResult 入力チェック結果
	 * @return 遷移先
	 */
	@PostMapping("/tasks")
	public String addTask(@Validated TaskForm taskForm, BindingResult bindingResult, Model model) {
		// 入力チェック
		if (bindingResult.hasErrors()) {
			// エラーがある場合は、投稿作成画面を再表示し、エラーメッセージを表示する
			return "add_task";
		}

		// タスクを保存する
		Tasks task = new Tasks();
		task.setTitle(taskForm.getTitle());
		task.setDetail(taskForm.getDetail());
		task.setDeadline(taskForm.getDeadline());
		repo.save(task);

		// 保存が成功した場合は、トップページにリダイレクトする
		return "redirect:/tasks";
	}

	/**
	 * タスクの新規作成画面.
	 * 
	 * @param model モデル
	 * @param date  追加対象日
	 * @return タスクの新規作成画面のンプレート名
	 */
	@GetMapping("/main/create/{date}")
	public String create(Model model, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

		return "create";
	}

}