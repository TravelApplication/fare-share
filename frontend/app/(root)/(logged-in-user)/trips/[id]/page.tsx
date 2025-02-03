type PageProps = {
  params: Promise<{ id: string }>;
};

async function Page({ params }: PageProps) {
  const resolvedParams = await params;
  return <div>details of trip with id: {`${resolvedParams.id}`}</div>;
}

export default Page;
