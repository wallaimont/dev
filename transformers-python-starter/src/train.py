from datasets import Dataset
from sklearn.model_selection import train_test_split
from transformers import (
    AutoModelForSequenceClassification,
    AutoTokenizer,
    DataCollatorWithPadding,
    Trainer,
    TrainingArguments,
)
import evaluate
import numpy as np


MODEL_NAME = "neuralmind/bert-base-portuguese-cased"
OUTPUT_DIR = "./model-output"


def build_toy_dataset() -> Dataset:
    texts = [
        "Atendimento excelente e resposta muito rapida",
        "Experiencia pessima com muitos atrasos",
        "Servico de alta qualidade e processo simples",
        "Comunicacao ruim e suporte lento",
        "Fiquei satisfeito com o resultado",
        "Estou frustrado com este servico",
        "A plataforma e intuitiva e confiavel",
        "O aplicativo trava com frequencia",
        "Onboarding muito bom e bem guiado",
        "Qualidade baixa e instrucoes confusas",
        "Tudo funcionou perfeitamente",
        "Nao pretendo usar novamente",
    ]
    labels = [1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0]

    return Dataset.from_dict({"text": texts, "label": labels})


def tokenize_dataset(dataset: Dataset, tokenizer: AutoTokenizer) -> Dataset:
    return dataset.map(lambda batch: tokenizer(batch["text"], truncation=True), batched=True)


def compute_metrics(eval_pred: tuple[np.ndarray, np.ndarray]) -> dict[str, float]:
    logits, labels = eval_pred
    predictions = np.argmax(logits, axis=-1)
    metric = evaluate.load("accuracy")
    return metric.compute(predictions=predictions, references=labels)


def main() -> None:
    dataset = build_toy_dataset()
    train_data, eval_data = train_test_split(dataset.to_list(), test_size=0.25, random_state=42)

    train_dataset = Dataset.from_list(train_data)
    eval_dataset = Dataset.from_list(eval_data)

    tokenizer = AutoTokenizer.from_pretrained(MODEL_NAME)
    tokenized_train = tokenize_dataset(train_dataset, tokenizer)
    tokenized_eval = tokenize_dataset(eval_dataset, tokenizer)

    model = AutoModelForSequenceClassification.from_pretrained(MODEL_NAME, num_labels=2)
    data_collator = DataCollatorWithPadding(tokenizer=tokenizer)

    args = TrainingArguments(
        output_dir=OUTPUT_DIR,
        evaluation_strategy="epoch",
        save_strategy="epoch",
        learning_rate=2e-5,
        per_device_train_batch_size=8,
        per_device_eval_batch_size=8,
        num_train_epochs=2,
        weight_decay=0.01,
        logging_steps=5,
        load_best_model_at_end=True,
        metric_for_best_model="accuracy",
        report_to="none",
    )

    trainer = Trainer(
        model=model,
        args=args,
        train_dataset=tokenized_train,
        eval_dataset=tokenized_eval,
        tokenizer=tokenizer,
        data_collator=data_collator,
        compute_metrics=compute_metrics,
    )

    trainer.train()
    trainer.save_model(OUTPUT_DIR)
    tokenizer.save_pretrained(OUTPUT_DIR)
    print(f"Modelo salvo em: {OUTPUT_DIR}")


if __name__ == "__main__":
    main()
