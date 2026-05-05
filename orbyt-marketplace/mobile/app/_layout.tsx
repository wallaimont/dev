import { Stack } from "expo-router";

export default function RootLayout() {
  return (
    <Stack
      screenOptions={{
        headerStyle: { backgroundColor: "#0f172a" },
        headerTintColor: "#f59e0b",
        headerTitleStyle: { fontWeight: "700" },
        headerTitle: "Orbyt Market"
      }}
    />
  );
}
