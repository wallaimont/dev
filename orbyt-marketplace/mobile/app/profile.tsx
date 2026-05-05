import { useEffect, useState } from "react";
import { Text, View, ScrollView, StyleSheet, TouchableOpacity, TextInput } from "react-native";
import { createUserAddress, deleteUserAddress, fetchUserAddresses, fetchUserProfile, updateUserAddress, updateUserProfile } from "../lib/api";
import { useSessionStore, type SessionState } from "../store/session-store";

export default function ProfileScreen() {
  const accessToken = useSessionStore((state: SessionState) => state.accessToken);
  const userId = useSessionStore((state: SessionState) => state.userId);
  const email = useSessionStore((state: SessionState) => state.email);
  const logout = useSessionStore((state: SessionState) => state.logout);
  const [profile, setProfile] = useState<Awaited<ReturnType<typeof fetchUserProfile>> | null>(null);
  const [addresses, setAddresses] = useState<Awaited<ReturnType<typeof fetchUserAddresses>>>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [profileName, setProfileName] = useState("");
  const [phone, setPhone] = useState("");
  const [cpf, setCpf] = useState("");
  const [language, setLanguage] = useState("pt-BR");
  const [currency, setCurrency] = useState("BRL");
  const [profileFeedback, setProfileFeedback] = useState<string | null>(null);
  const [isSavingProfile, setIsSavingProfile] = useState(false);
  const [addressLabel, setAddressLabel] = useState("");
  const [addressStreet, setAddressStreet] = useState("");
  const [addressNumber, setAddressNumber] = useState("");
  const [addressNeighborhood, setAddressNeighborhood] = useState("");
  const [addressCity, setAddressCity] = useState("");
  const [addressState, setAddressState] = useState("");
  const [addressZipCode, setAddressZipCode] = useState("");
  const [editingAddressId, setEditingAddressId] = useState<string | null>(null);
  const [addressFeedback, setAddressFeedback] = useState<string | null>(null);
  const [isSavingAddress, setIsSavingAddress] = useState(false);

  useEffect(() => {
    if (!accessToken || !userId) {
      setProfile(null);
      setAddresses([]);
      return;
    }

    setIsLoading(true);
    Promise.all([fetchUserProfile(accessToken, userId), fetchUserAddresses(accessToken, userId)])
      .then(([profileData, addressData]) => {
        setProfile(profileData);
        setAddresses(addressData);
        setProfileName(profileData.displayName ?? "");
        setPhone(profileData.phone ?? "");
        setCpf(profileData.cpf ?? "");
        setLanguage(profileData.preferredLanguage ?? "pt-BR");
        setCurrency(profileData.preferredCurrency ?? "BRL");
      })
      .catch(() => {
        setProfile(null);
        setAddresses([]);
      })
      .finally(() => setIsLoading(false));
  }, [accessToken, userId]);

  const displayName = profile?.displayName || profileName || email?.split("@")[0] || "Usuário Orbyt";
  const avatarLetters = displayName
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join("") || "OM";

  async function handleSaveProfile() {
    if (!accessToken || !userId) {
      setProfileFeedback("Conecte a conta demo para editar o perfil.");
      return;
    }

    setIsSavingProfile(true);
    try {
      const updated = await updateUserProfile(accessToken, userId, {
        displayName: profileName.trim(),
        phone: phone.trim(),
        cpf: cpf.trim(),
        preferredLanguage: language.trim() || "pt-BR",
        preferredCurrency: currency.trim() || "BRL",
      });
      setProfile(updated);
      setProfileFeedback("Perfil atualizado com sucesso.");
    } catch {
      setProfileFeedback("Não foi possível atualizar o perfil.");
    } finally {
      setIsSavingProfile(false);
    }
  }

  async function handleAddAddress() {
    if (!accessToken || !userId) {
      setAddressFeedback("Conecte a conta demo para cadastrar endereço.");
      return;
    }
    if (!addressLabel || !addressStreet || !addressNeighborhood || !addressCity || !addressState || !addressZipCode) {
      setAddressFeedback("Preencha os campos obrigatórios do endereço.");
      return;
    }

    setIsSavingAddress(true);
    try {
      const payload = {
        label: addressLabel.trim(),
        street: addressStreet.trim(),
        number: addressNumber.trim(),
        neighborhood: addressNeighborhood.trim(),
        city: addressCity.trim(),
        state: addressState.trim(),
        zipCode: addressZipCode.trim(),
        defaultAddress: editingAddressId ? false : addresses.length === 0,
      };

      if (editingAddressId) {
        const updated = await updateUserAddress(accessToken, userId, editingAddressId, payload);
        setAddresses((current) => current.map((address) => address.id === editingAddressId ? updated : address));
        setAddressFeedback("Endereço atualizado com sucesso.");
      } else {
        const created = await createUserAddress(accessToken, userId, payload);
        setAddresses((current) => [created, ...current]);
        setAddressFeedback("Endereço cadastrado com sucesso.");
      }

      setEditingAddressId(null);
      setAddressLabel("");
      setAddressStreet("");
      setAddressNumber("");
      setAddressNeighborhood("");
      setAddressCity("");
      setAddressState("");
      setAddressZipCode("");
    } catch {
      setAddressFeedback(editingAddressId ? "Não foi possível atualizar o endereço." : "Não foi possível cadastrar o endereço.");
    } finally {
      setIsSavingAddress(false);
    }
  }

  function handleEditAddress(addressId: string) {
    const address = addresses.find((item) => item.id === addressId);
    if (!address) {
      return;
    }
    setEditingAddressId(address.id);
    setAddressLabel(address.label);
    setAddressStreet(address.street);
    setAddressNumber(address.number ?? "");
    setAddressNeighborhood(address.neighborhood);
    setAddressCity(address.city);
    setAddressState(address.state);
    setAddressZipCode(address.zipCode);
    setAddressFeedback(null);
  }

  async function handleDeleteAddress(addressId: string) {
    if (!accessToken || !userId) {
      setAddressFeedback("Conecte a conta demo para excluir endereço.");
      return;
    }
    try {
      await deleteUserAddress(accessToken, userId, addressId);
      setAddresses((current) => current.filter((address) => address.id !== addressId));
      if (editingAddressId === addressId) {
        setEditingAddressId(null);
        setAddressLabel("");
        setAddressStreet("");
        setAddressNumber("");
        setAddressNeighborhood("");
        setAddressCity("");
        setAddressState("");
        setAddressZipCode("");
      }
      setAddressFeedback("Endereço removido com sucesso.");
    } catch {
      setAddressFeedback("Não foi possível excluir o endereço.");
    }
  }

  return (
    <ScrollView style={s.container}>
      <View style={s.header}>
        <View style={s.avatar}>
          <Text style={s.avatarText}>{avatarLetters}</Text>
        </View>
        <Text style={s.name}>{displayName}</Text>
        <Text style={s.email}>{email ?? "Conta não conectada"}</Text>
      </View>

      <View style={s.section}>
        {!accessToken ? (
          <Text style={s.empty}>Entre com a conta demo para ver seu perfil.</Text>
        ) : isLoading ? (
          <Text style={s.empty}>Carregando perfil...</Text>
        ) : (
          <>
            <View style={s.formCard}>
              <Text style={s.formTitle}>Dados pessoais</Text>
              <TextInput style={s.input} value={profileName} onChangeText={setProfileName} placeholder="Nome" placeholderTextColor="#94a3b8" />
              <TextInput style={s.input} value={phone} onChangeText={setPhone} placeholder="Telefone" placeholderTextColor="#94a3b8" />
              <TextInput style={s.input} value={cpf} onChangeText={setCpf} placeholder="CPF" placeholderTextColor="#94a3b8" />
              <TextInput style={s.input} value={language} onChangeText={setLanguage} placeholder="Idioma" placeholderTextColor="#94a3b8" />
              <TextInput style={s.input} value={currency} onChangeText={setCurrency} placeholder="Moeda" placeholderTextColor="#94a3b8" />
              <TouchableOpacity style={s.primaryButton} onPress={handleSaveProfile} disabled={isSavingProfile}>
                <Text style={s.primaryButtonText}>{isSavingProfile ? "Salvando..." : "Salvar perfil"}</Text>
              </TouchableOpacity>
              {profileFeedback ? <Text style={s.feedback}>{profileFeedback}</Text> : null}
            </View>

            <View style={s.formCard}>
              <Text style={s.formTitle}>{editingAddressId ? "Editar endereço" : "Novo endereço"}</Text>
              <TextInput style={s.input} value={addressLabel} onChangeText={setAddressLabel} placeholder="Label" placeholderTextColor="#94a3b8" />
              <TextInput style={s.input} value={addressStreet} onChangeText={setAddressStreet} placeholder="Rua" placeholderTextColor="#94a3b8" />
              <TextInput style={s.input} value={addressNumber} onChangeText={setAddressNumber} placeholder="Número" placeholderTextColor="#94a3b8" />
              <TextInput style={s.input} value={addressNeighborhood} onChangeText={setAddressNeighborhood} placeholder="Bairro" placeholderTextColor="#94a3b8" />
              <TextInput style={s.input} value={addressCity} onChangeText={setAddressCity} placeholder="Cidade" placeholderTextColor="#94a3b8" />
              <TextInput style={s.input} value={addressState} onChangeText={setAddressState} placeholder="UF" placeholderTextColor="#94a3b8" />
              <TextInput style={s.input} value={addressZipCode} onChangeText={setAddressZipCode} placeholder="CEP" placeholderTextColor="#94a3b8" />
              <TouchableOpacity style={s.primaryButton} onPress={handleAddAddress} disabled={isSavingAddress}>
                <Text style={s.primaryButtonText}>{isSavingAddress ? "Salvando..." : editingAddressId ? "Salvar endereço" : "Cadastrar endereço"}</Text>
              </TouchableOpacity>
              {editingAddressId ? (
                <TouchableOpacity style={s.secondaryButton} onPress={() => {
                  setEditingAddressId(null);
                  setAddressLabel("");
                  setAddressStreet("");
                  setAddressNumber("");
                  setAddressNeighborhood("");
                  setAddressCity("");
                  setAddressState("");
                  setAddressZipCode("");
                  setAddressFeedback(null);
                }}>
                  <Text style={s.secondaryButtonText}>Cancelar edição</Text>
                </TouchableOpacity>
              ) : null}
              {addressFeedback ? <Text style={s.feedback}>{addressFeedback}</Text> : null}
            </View>

            <MenuItem label={`Endereços: ${addresses.length}`} />
            {addresses.map((address) => (
              <View key={address.id} style={s.addressCard}>
                <Text style={s.addressLabel}>{address.label}</Text>
                <Text style={s.addressMeta}>{address.street}, {address.number || "s/n"}</Text>
                <Text style={s.addressMeta}>{address.city}/{address.state} • {address.zipCode}</Text>
                <View style={s.addressActions}>
                  <TouchableOpacity onPress={() => handleEditAddress(address.id)}>
                    <Text style={s.addressActionPrimary}>Editar</Text>
                  </TouchableOpacity>
                  <TouchableOpacity onPress={() => handleDeleteAddress(address.id)}>
                    <Text style={s.addressActionDanger}>Excluir</Text>
                  </TouchableOpacity>
                </View>
              </View>
            ))}
            <MenuItem label="Sair" danger onPress={logout} />
          </>
        )}
      </View>
    </ScrollView>
  );
}

function MenuItem({ label, danger, onPress }: { label: string; danger?: boolean; onPress?: () => void }) {
  return (
    <TouchableOpacity style={s.menuItem} onPress={onPress} disabled={!onPress}>
      <Text style={[s.menuLabel, danger && s.danger]}>{label}</Text>
      <Text style={s.arrow}>›</Text>
    </TouchableOpacity>
  );
}

const s = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f8fafc" },
  header: { alignItems: "center", padding: 32, backgroundColor: "#0f172a" },
  avatar: {
    width: 72,
    height: 72,
    borderRadius: 36,
    backgroundColor: "#f59e0b",
    alignItems: "center",
    justifyContent: "center"
  },
  avatarText: { fontSize: 24, fontWeight: "700", color: "#0f172a" },
  name: { fontSize: 20, fontWeight: "700", color: "white", marginTop: 12 },
  email: { fontSize: 14, color: "#94a3b8", marginTop: 4 },
  section: { padding: 16 },
  formCard: {
    backgroundColor: "white",
    borderRadius: 12,
    padding: 16,
    marginBottom: 12
  },
  formTitle: { fontSize: 15, fontWeight: "700", color: "#0f172a", marginBottom: 12 },
  input: {
    backgroundColor: "#f8fafc",
    borderRadius: 10,
    padding: 12,
    fontSize: 14,
    color: "#0f172a",
    borderWidth: 1,
    borderColor: "#e2e8f0",
    marginBottom: 10
  },
  primaryButton: {
    backgroundColor: "#0f172a",
    borderRadius: 10,
    padding: 12,
    alignItems: "center"
  },
  secondaryButton: {
    backgroundColor: "#e2e8f0",
    borderRadius: 10,
    padding: 12,
    alignItems: "center",
    marginTop: 10
  },
  primaryButtonText: { color: "white", fontSize: 14, fontWeight: "700" },
  secondaryButtonText: { color: "#0f172a", fontSize: 14, fontWeight: "700" },
  feedback: { fontSize: 13, color: "#475569", marginTop: 10 },
  addressCard: {
    backgroundColor: "white",
    borderRadius: 12,
    padding: 16,
    marginBottom: 10
  },
  addressLabel: { fontSize: 15, fontWeight: "700", color: "#0f172a" },
  addressMeta: { fontSize: 13, color: "#64748b", marginTop: 4 },
  addressActions: { flexDirection: "row", gap: 16, marginTop: 10 },
  addressActionPrimary: { fontSize: 13, color: "#f59e0b", fontWeight: "700" },
  addressActionDanger: { fontSize: 13, color: "#ef4444", fontWeight: "700" },
  menuItem: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    backgroundColor: "white",
    borderRadius: 12,
    padding: 16,
    marginBottom: 8
  },
  menuLabel: { fontSize: 15, color: "#0f172a" },
  danger: { color: "#ef4444" },
  arrow: { fontSize: 20, color: "#94a3b8" },
  empty: { fontSize: 14, color: "#64748b" }
});
