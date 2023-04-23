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

	@GetMapping("/tasks")
	public String getCalendar(Model model) {
		List<Tasks> list = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("tasks", list);

		List<List<LocalDate>> month = new ArrayList<>();

		LocalDate today = LocalDate.now();
		LocalDate firstDayOfMonth = today.withDayOfMonth(1);

		int daysInMonth = firstDayOfMonth.lengthOfMonth();
		DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();
		int offset = firstDayOfWeek.getValue() % 7;

		LocalDate start = firstDayOfMonth.minusDays(offset);
		LocalDate end = firstDayOfMonth.plusDays(daysInMonth).plusDays(6 - ((daysInMonth + offset - 1) % 7));

		LocalDate currentDay = start;
		while (currentDay.isBefore(end.plusDays(1))) {
			List<LocalDate> week = new ArrayList<>();
			for (int i = 0; i < 7; i++) {
				week.add(currentDay);
			}
			month.add(week);
		}

		MultiValueMap<LocalDate, Tasks> tasks = new LinkedMultiValueMap<>();
		for (Tasks task : list) {
			LocalDate date = task.getDeadline().toLocalDate();
			tasks.add(date, task);
		}

		model.addAttribute("matrix", month);
		model.addAttribute("tasks", tasks);
		return "main";
	}


	/**
	 * 投稿を削除する
	 * 
	 * @param id 投稿ID
	 * @return 遷移先
	 */
	  @PostMapping("/tasks/create")
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
		 * タスクの新規作成画面.
		 * 
		 * @param model モデル
		 * @param date  追加対象日
		 * @return タスクの新規作成画面のテンプレート名
		 */
		@GetMapping("/main/create/{date}")
		public String create(Model model, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

			return "create";
		}

		/**
		 * 投稿を削除する
		 * 
		 * @param id 投稿ID
		 * @return 遷移先
		 */
		

		@PostMapping("/tasks/delete/{id}")
		public String delete(@PathVariable Integer id) {
			repo.deleteById(id);
			return "redirect:/tasks";
		}
	}
	  
	  
