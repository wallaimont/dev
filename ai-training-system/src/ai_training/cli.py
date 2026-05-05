from __future__ import annotations

import argparse
import json

import uvicorn

from .service import predict_from_model, train_from_config


def _build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        prog="ai-trainer",
        description="Train and run AI models for tabular data",
    )
    subparsers = parser.add_subparsers(dest="command", required=True)

    train_parser = subparsers.add_parser("train", help="Train model from YAML config")
    train_parser.add_argument("--config", required=True, help="Path to YAML config file")

    predict_parser = subparsers.add_parser("predict", help="Run batch prediction from a trained model")
    predict_parser.add_argument("--model", required=True, help="Path to saved model artifact")
    predict_parser.add_argument("--input", required=True, help="Path to CSV input file")
    predict_parser.add_argument("--output", required=True, help="Path to CSV output with predictions")

    serve_parser = subparsers.add_parser("serve", help="Run REST API server")
    serve_parser.add_argument("--host", default="0.0.0.0", help="API host")
    serve_parser.add_argument("--port", type=int, default=8000, help="API port")

    return parser


def main() -> None:
    parser = _build_parser()
    args = parser.parse_args()

    if args.command == "train":
        result = train_from_config(args.config)
        print(json.dumps(result, indent=2))
        return

    if args.command == "predict":
        result = predict_from_model(args.model, args.input, args.output)
        print(json.dumps(result, indent=2))
        return

    if args.command == "serve":
        uvicorn.run("ai_training.api:app", host=args.host, port=args.port, reload=False)
        return

    raise ValueError(f"Unsupported command: {args.command}")


if __name__ == "__main__":
    main()
