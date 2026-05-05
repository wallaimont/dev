from transformers import pipeline


def main() -> None:
    classifier = pipeline(
        task="sentiment-analysis",
        model="nlptown/bert-base-multilingual-uncased-sentiment",
    )

    texts = [
        "Adorei usar Transformers com Python.",
        "Minha experiencia com o suporte foi muito ruim.",
    ]

    results = classifier(texts)
    for text, result in zip(texts, results):
        print(f"Text: {text}")
        print(f"Label: {result['label']} | Score: {result['score']:.4f}")
        print("-" * 50)


if __name__ == "__main__":
    main()
